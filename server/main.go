package main

import (
	"fmt"

	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/database"
	"github.com/TudorEsan/QPerior-Hackhaton/routes"
	"github.com/gin-contrib/cors"
	"github.com/gin-gonic/gin"
	"github.com/hashicorp/go-hclog"
)

func main() {
	mongoClient := database.DbInstace()
	l := hclog.Default()
	router := gin.Default()

	// disable cors
	config := cors.DefaultConfig()
	config.AllowCredentials = true
	config.AllowHeaders = []string{"Content-Type", "Authorization", "Origin", "Accept", "Access-Control-Allow-Origin", "token"}
	config.AllowOrigins = []string{"http://localhost:3000", "http://financeapp.tudoresan.ro:3000/"}
	config.AllowMethods = []string{"GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"}
	// config.AllowOrigins = []string{"*"}

	router.Use(cors.New(config))
	apiGroup := router.Group("/")

	apiGroup.GET("/ping", func(c *gin.Context) {
		fmt.Println("ping")
		c.JSON(200, gin.H{
			"message": "pong",
		})
	})

	userController := controllers.NewUserController(l, mongoClient)
	roomsController := controllers.NewRoomController(l, mongoClient)
	userGroup := router.Group("")
	roomsGroup := router.Group("")
	reservationGroup := router.Group("")

	routes.InitUserRoutes(userGroup, userController)
	routes.InitRoomRoutes(roomsGroup, roomsController)
	routes.InitReservationRoutes(reservationGroup, controllers.NewReservationController(l, mongoClient))

	router.Run()
}
