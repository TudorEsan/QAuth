package middlewares

import (
	"net/http"

	"github.com/TudorEsan/QPerior-Hackhaton/customErrors"
	"github.com/TudorEsan/QPerior-Hackhaton/helpers"
	"github.com/gin-gonic/gin"
)

func RemoveCookies(c *gin.Context) {
	c.SetCookie("token", "", 60*60*24*30, "", "", false, false)
	c.SetCookie("refreshToken", "", 60*60*24*30, "", "", false, false)
}

func VerifyAuth() gin.HandlerFunc {
	return func(c *gin.Context) {

		// Check if token exists
		token, err := c.Cookie("token")
		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Token Not Found"})
			RemoveCookies(c)
			c.Abort()
			return
		}
		// Check if Refresh Token exists
		_, err = c.Cookie("refreshToken")
		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Refresh Token Not Found"})
			RemoveCookies(c)
			c.Abort()
			return
		}

		// Validate Token
		claims, err := helpers.ValidateToken(token)
		if _, ok := err.(customErrors.ExpiredToken); ok {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Token Expired"})
			RemoveCookies(c)
			c.Abort()
			return
		}

		if _, ok := err.(customErrors.EmailNotValidated); ok {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Email Not Validated"})
			RemoveCookies(c)
			c.Abort()
			return
		}

		if _, ok := err.(customErrors.InvalidToken); ok {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Invalid Token"})
			RemoveCookies(c)
			c.Abort()
			return
		}

		if err != nil {
			c.JSON(http.StatusUnauthorized, gin.H{"message": "Invalid Token"})
			RemoveCookies(c)
			c.Abort()
			return
		}
		c.Set("claims", claims)
		c.Set("UserId", claims.Id)
		c.Next()
	}

}
