package routes

import (
	"github.com/TudorEsan/QPerior-Hackhaton/controllers"
	"github.com/gin-gonic/gin"
)

func InitUserRoutes(r *gin.RouterGroup, c *controllers.UserController) {
	r.POST("/login", c.LoginHandler())
	r.POST("/register", c.SignupHandler())
	// r.POST("/logout", c.Logout)
}