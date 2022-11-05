package routes

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/middlewares"
	"github.com/gin-gonic/gin"
)

func InitRoomRoutes(r *gin.RouterGroup, c *controllers.RoomController) {
	r.GET("room/ws", c.ConnectRoom())

	r.Use(middlewares.VerifyAuth())
	r.POST("/room", c.AddRoomHandler())
	r.GET("/rooms", c.GetRooms())
	r.DELETE("/room/:id", c.DeleteRoomHandler())
	r.GET("/room/:id", c.OpenRoom())
}
