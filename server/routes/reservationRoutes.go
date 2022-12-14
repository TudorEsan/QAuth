package routes

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/middlewares"
	"github.com/gin-gonic/gin"
)

func InitReservationRoutes(r *gin.RouterGroup, c *controllers.ReservationController) {
	r.Use(middlewares.VerifyAuth())
	r.GET("/allReservations", c.GetReservations())
	r.GET("/reservations", c.GetUserReservations())
	r.GET("reservationFilter", c.FilterReservations())
	r.POST("/reservation/:from", c.AddReservation())
	r.DELETE("/reservation/:id", c.DeleteReservation())
	// r.DELETE("/reservation/:id", c.DeleteReservation())
}
