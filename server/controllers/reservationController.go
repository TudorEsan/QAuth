package controllers

import (
	"context"
	"strconv"
	"time"

	"github.com/TudorEsan/QPerior-Hackhaton/database"
	"github.com/TudorEsan/QPerior-Hackhaton/helpers"
	"github.com/TudorEsan/QPerior-Hackhaton/models"
	"github.com/gin-gonic/gin"
	"github.com/hashicorp/go-hclog"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type ReservationController struct {
	l                     hclog.Logger
	userCollection        *mongo.Collection
	reservationCollection *mongo.Collection
	roomsCollection       *mongo.Collection
}

func NewReservationController(l hclog.Logger, mongoClient *mongo.Client) *ReservationController {
	userCollection := database.OpenCollection(mongoClient, "users")
	reservationCollection := database.OpenCollection(mongoClient, "reservations")
	roomCollection := database.OpenCollection(mongoClient, "rooms")
	return &ReservationController{l, userCollection, reservationCollection, roomCollection}
}

func (controller *ReservationController) AddReservation() gin.HandlerFunc {
	return func(c *gin.Context) {
		var reservationForm models.ReservationForm
		if err := c.Bind(&reservationForm); err != nil {
			controller.l.Error("Could not bind reservation", "error", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		from := c.Param("from")
		controller.l.Info("PARSING", "FROM", from)
		fromDate, err := time.Parse("2006-01-02T15:04:05", from)
		if err != nil {
			controller.l.Error("Parsing", "error", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}
		reservationForm.From = fromDate

		reservation := models.NewReservation(reservationForm)
		userId, _ := primitive.ObjectIDFromHex(c.MustGet("claims").(*models.SignedDetails).Id)
		reservation.UserId = userId

		// validate room
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		var room models.Room
		err = controller.roomsCollection.FindOne(ctx, bson.M{"_id": reservation.RoomId}).Decode(&room)
		if err != nil {
			controller.l.Error("Could not get room", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		reservation.Id = primitive.NewObjectID()

		reservationFromThatDay, err := helpers.GetReservationsFromDate(controller.reservationCollection, reservation.From.Year(), int(reservation.From.Month()), reservation.From.Day(), reservation.RoomId)
		if err != nil {
			controller.l.Error("Could not get reservations from that day", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		reservationAvalabile := helpers.IsReservationValid(reservation, reservationFromThatDay)
		if !reservationAvalabile {
			controller.l.Error("Reservation is not valid", err)
			c.JSON(400, gin.H{"message": "Reservation is not valid"})
			return
		}

		_, err = controller.reservationCollection.InsertOne(ctx, reservation)
		if err != nil {
			controller.l.Error("Could not add reservation", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, gin.H{"message": "Reservation added", "reservation": reservation})
		// get all reservations
		// return reservations
	}
}

func (controller *ReservationController) GetReservations() gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		var reservations []models.Reservation
		reservations = make([]models.Reservation, 0)
		cursor, err := controller.reservationCollection.Find(ctx, bson.M{})
		if err != nil {
			controller.l.Error("Could not get reservations", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		defer cursor.Close(ctx)
		for cursor.Next(ctx) {
			var reservation models.Reservation
			cursor.Decode(&reservation)
			reservations = append(reservations, reservation)
		}

		c.JSON(200, reservations)
	}
}

func (controller *ReservationController) FilterReservations() gin.HandlerFunc {
	return func(c *gin.Context) {
		var day, month, year int
		// get date from query
		day, _ = strconv.Atoi(c.Query("day"))
		month, _ = strconv.Atoi(c.Query("month"))
		year, _ = strconv.Atoi(c.Query("year"))

		reservations, err := helpers.GetReservationsFromDate(controller.reservationCollection, year, month, day, primitive.NilObjectID)
		if err != nil {
			controller.l.Error("Could not get reservations", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, reservations)
	}
}

func (controller *ReservationController) GetUserReservations() gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		claims := c.MustGet("claims").(models.SignedDetails)
		idS := claims.Id

		userId, err := primitive.ObjectIDFromHex(idS)
		if err != nil {
			controller.l.Error("Could not get user id", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		var reservation models.Reservation
		err = controller.reservationCollection.FindOne(ctx, bson.M{"userId": userId}).Decode(&reservation)
		if err != nil {
			controller.l.Error("Could not get reservation", err)
			c.JSON(400, gin.H{"message": err.Error()})
			return
		}

		c.JSON(200, reservation)
	}
}
