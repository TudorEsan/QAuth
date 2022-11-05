package main

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/database"
	"github.com/TudorEsan/QPerior-Hackhaton/routes"
	"github.com/gin-gonic/gin"
	"github.com/hashicorp/go-hclog"
)

func main() {
	mongoClient := database.DbInstace()
	l := hclog.Default()
	router := gin.Default()

	// cors allow all
	router.Use(func(c *gin.Context) {
		c.Writer.Header().Set("Access-Control-Allow-Origin", "*")
		c.Writer.Header().Set("Access-Control-Allow-Credentials", "true")
		c.Writer.Header().Set("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, UPDATE")
		c.Writer.Header().Set("Access-Control-Allow-Headers", "Origin, Content-Type, Content-Length, Accept-Encoding, X-CSRF-Token, Authorization, accept, client-security-token")
		c.Writer.Header().Set("Access-Control-Expose-Headers", "Content-Length")
		c.Writer.Header().Set("Access-Control-Max-Age", "86400")
		c.Writer.Header().Set("Content-Type", "application/json; charset=utf-8")

		if c.Request.Method == "OPTIONS" {
			c.AbortWithStatus(204)
			return
		}

		c.Next()
	})

	userController := controllers.NewUserController(l, mongoClient)
	roomsController := controllers.NewRoomController(l, mongoClient)
	routes.InitUserRoutes(router.Group("/"), userController)
	routes.InitRoomRoutes(router.Group("/"), roomsController)
	routes.InitReservationRoutes(router.Group("/"), controllers.NewReservationController(l, mongoClient))

	router.Run()
}
