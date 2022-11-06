package routes

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/middlewares"
	"github.com/gin-gonic/gin"
)

func InitUserRoutes(r *gin.RouterGroup, c *controllers.UserController) {
	r.POST("login", c.LoginHandler())
	r.Use(middlewares.VerifyAuth())
	r.GET("users", c.GetUsers())
	r.DELETE("users/:id", c.DeleteUser())

	r.Use(middlewares.VerifyAuth())
	r.POST("register", c.SignupHandler())
	// r.POST("/logout", c.Logout)
}
