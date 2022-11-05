package routes

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/TudorEsan/QPerior-Hackhaton/middlewares"
	"github.com/gin-gonic/gin"
)

func InitUserRoutes(r *gin.RouterGroup, c *controllers.UserController) {
	r.POST("login", c.LoginHandler())
	r.POST("register", c.SignupHandler())
	r.Use(middlewares.VerifyAuth())
	r.GET("users", c.GetUsers())
	// r.POST("/logout", c.Logout)
}
