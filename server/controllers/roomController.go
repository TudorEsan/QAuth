package controllers

import (
	"context"
	"fmt"
	"net/http"
	"time"

	"github.com/TudorEsan/QPerior-Hackhaton/database"
	"github.com/TudorEsan/QPerior-Hackhaton/helpers"
	"github.com/TudorEsan/QPerior-Hackhaton/models"
	"github.com/gin-gonic/gin"
	"github.com/gorilla/websocket"
	"github.com/hashicorp/go-hclog"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type RoomController struct {
	l                      hclog.Logger
	userCollection         *mongo.Collection
	roomCollection         *mongo.Collection
	reservationsCollection *mongo.Collection
	roomsConnected         map[string]*websocket.Conn
}

func NewRoomController(l hclog.Logger, mongoClient *mongo.Client) *RoomController {
	userCollection := database.OpenCollection(mongoClient, "users")
	roomCollection := database.OpenCollection(mongoClient, "rooms")
	reservationCollection := database.OpenCollection(mongoClient, "reservations")
	roomsConnected := make(map[string]*websocket.Conn)
	return &RoomController{l, userCollection, roomCollection, reservationCollection, roomsConnected}
}

func OpenRoom(conn *websocket.Conn) {
	conn.WriteJSON(gin.H{"message": "open"}) 
}

func CloseRoom(conn *websocket.Conn) {
	conn.WriteJSON(gin.H{"message": "unauthorized"})
}

func (controller *RoomController) OpenRoom() gin.HandlerFunc {
	return func(c *gin.Context) {
		roomId := c.Param("id")
		// validate room is in db
		id, err := primitive.ObjectIDFromHex(roomId)
		if err != nil {
			controller.l.Error("Could not get room", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()
		var room models.Room
		err = controller.roomCollection.FindOne(ctx, bson.M{"_id": id}).Decode(&room)
		if err != nil {
			controller.l.Error("Could not get room", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}
		conn, ok := controller.roomsConnected[roomId]
		if !ok {
			controller.l.Error("Room offline", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": "Room offline"})
			return
		}

		activeReservation, err := helpers.GetActiveReservation(controller.reservationsCollection, id)
		if err != nil {
			controller.l.Error("Could not get active reservation", err)
			err := helpers.ValidateRole(c, room.MinimalRole)
			if err != nil {
				// c.JSON(http.StatusUnauthorized, gin.H{"message": "You are not authorized to open this room"})
				CloseRoom(conn)
				return
			}
			OpenRoom(conn)
			c.JSON(200, gin.H{"message": "Room opened"})
			return
		}
		if activeReservation.UserId == helpers.GetUserId(c) {
			OpenRoom(conn)
			c.JSON(200, gin.H{"message": "Room opened"})
			return
		}

		for _, userId := range activeReservation.Guests {
			if userId == helpers.GetUserId(c) {
				OpenRoom(conn)
				c.JSON(200, gin.H{"message": "Room opened"})
				return
			}
		}

		if helpers.GetUserRole(c) == 0 {
			OpenRoom(conn)
			c.JSON(200, gin.H{"message": "Room opened"})
			return
		}
		
		CloseRoom(conn)
		c.JSON(http.StatusUnauthorized, gin.H{"message": "You are not authorized to open this room"})
	}
}

func (controller *RoomController) AddRoomHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		var room models.Room
		controller.l.Info("Add room handler")
		if err := c.BindJSON(&room); err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error()})
			return
		}
		room = room.WithId()

		err := helpers.ValidateRole(c, 0)
		if err != nil {
			controller.l.Error("Role not enough", err)
			return
		}

		// insert room in the db
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		_, err = controller.roomCollection.InsertOne(ctx, room)
		if err != nil {
			controller.l.Error("Could not insert room", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, room)
	}
}

func (controller *RoomController) DeleteRoomHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		err := helpers.ValidateRole(c, 0)
		if err != nil {
			return
		}

		idS := c.Param("id")

		// delete room from the db
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		id, err := primitive.ObjectIDFromHex(idS)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error()})
			return
		}

		_, err = controller.roomCollection.DeleteOne(ctx, bson.M{"_id": id})
		if err != nil {
			controller.l.Error("Could not delete room", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, gin.H{"message": "Room deleted"})
	}
}

func (controller *RoomController) GetRooms() gin.HandlerFunc {
	return func(c *gin.Context) {
		helpers.ValidateRole(c, 0)

		// get rooms from the db
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		var rooms []models.Room
		cursor, err := controller.roomCollection.Find(ctx, bson.M{})
		if err != nil {
			controller.l.Error("Could not get rooms", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		if err = cursor.All(ctx, &rooms); err != nil {
			controller.l.Error("Could not get rooms", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, rooms)
	}
}

var wsupgrader = websocket.Upgrader{
	ReadBufferSize:  1024,
	WriteBufferSize: 1024,
}

func (controller RoomController) wshandler(w http.ResponseWriter, r *http.Request) {
	conn, err := wsupgrader.Upgrade(w, r, nil)
	if err != nil {
		fmt.Println("Failed to set websocket upgrade: %+v", err)
		return
	}

	id := r.URL.Query().Get("id")
	if id == "" {
		fmt.Println("No id")
		conn.Close()
		w.WriteHeader(http.StatusBadRequest)
		// return 400 and close connection
		return
	}
	controller.l.Info("Room connected", "id", id)
	controller.roomsConnected[id] = conn
	keepAlive(conn, time.Minute * 2)

}

func keepAlive(c *websocket.Conn, timeout time.Duration) {
	lastResponse := time.Now()
	c.SetPongHandler(func(msg string) error {
		 lastResponse = time.Now()
		 return nil
 })

 go func() {
	 for {
			err := c.WriteMessage(websocket.PingMessage, []byte("keepalive"))
			if err != nil {
					return 
			}   
			time.Sleep(timeout/2)
			if(time.Since(lastResponse) > timeout) {
					c.Close()
					return
			}
	}
}()
}

func (controller *RoomController) ConnectRoom() gin.HandlerFunc {
	return func(c *gin.Context) {

		controller.wshandler(c.Writer, c.Request)
	}
}
