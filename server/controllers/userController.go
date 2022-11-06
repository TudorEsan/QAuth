package controllers

import (
	"context"
	"net/http"
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

type UserController struct {
	l              hclog.Logger
	userCollection *mongo.Collection
}

func NewUserController(l hclog.Logger, mongoClient *mongo.Client) *UserController {
	userCollection := database.OpenCollection(mongoClient, "users")
	return &UserController{l, userCollection}
}

func (controller *UserController) DeleteUser() gin.HandlerFunc {
	return func(c *gin.Context) {
		idS := c.Param("id")
		if idS == "" {
			c.JSON(http.StatusBadRequest, gin.H{"error": "id is required"})
			return
		}
		id, err := primitive.ObjectIDFromHex(idS)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"error": "invalid id"})
			return
		}

		err = helpers.ValidateRole(c, 0)
		if err != nil {
			return
		}

		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()
		_, err = controller.userCollection.DeleteOne(ctx, bson.M{"_id": id})
		if err != nil {
			controller.l.Error("Could not delete user", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		c.JSON(http.StatusOK, gin.H{"message": "User deleted"})

	}
}

func (controller *UserController) SignupHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()
		var user models.UserSignupForm
		if err := c.BindJSON(&user); err != nil {
			controller.l.Error("Could not bind", err)
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error(), "body": c.Request.Body})
			return
		}

		err := helpers.ValidateRole(c, 0)
		if err != nil {
			return
		}

		// check if username is not present in the database
		err = helpers.ValidEmail(ctx, controller.userCollection, user.Email)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error()})
			return
		}

		userForDb := models.NewUser(user)

		// insert user in the db
		ctx, cancel = context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()

		_, err = controller.userCollection.InsertOne(ctx, userForDb)
		if err != nil {
			controller.l.Error("Could not insert user", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}

		c.JSON(http.StatusOK, gin.H{
			"ok": "hehe",
		})
	}

}

func (controller *UserController) GetUsers() gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*15)
		defer cancel()
		var users []models.UserReturn
		cursor, err := controller.userCollection.Find(ctx, bson.M{})
		if err != nil {
			controller.l.Error("Could not get users", err)
			c.JSON(http.StatusInternalServerError, gin.H{"message": err.Error()})
			return
		}
		defer cursor.Close(ctx)

		for cursor.Next(ctx) {
			var user models.User
			cursor.Decode(&user)
			controller.l.Info("email", c.MustGet("claims").(*models.SignedDetails).Email)
			if user.Email != c.MustGet("claims").(*models.SignedDetails).Email {
				users = append(users, models.NewUserReturn(user))
			}
		}
		c.JSON(http.StatusOK, users)
	}
}

func (controller *UserController) LoginHandler() gin.HandlerFunc {
	return func(c *gin.Context) {
		ctx, cancel := context.WithTimeout(context.Background(), time.Second*20)
		defer cancel()
		var user models.UserLoginForm
		var foundUser models.User

		if err := c.BindJSON(&user); err != nil {
			controller.l.Error("Could not bind", err)
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error()})
			return
		}

		err := controller.userCollection.FindOne(ctx, bson.M{"email": user.Email}).Decode(&foundUser)
		if err != nil {
			controller.l.Error("Email does not exist", err)
			c.JSON(http.StatusBadRequest, gin.H{"message": "Username does not exist"})
			return
		}

		err = helpers.CheckPassword(foundUser, user)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"message": err.Error()})
			return
		}

		jwt, refreshToken, err := helpers.GenerateTokens(foundUser)
		if err != nil {
			c.JSON(http.StatusBadRequest, gin.H{"message": "Could not generate tokens"})
			return
		}

		helpers.SetCookies(c, jwt, refreshToken)
		c.JSON(http.StatusOK, gin.H{"user": foundUser, "token": jwt, "refreshToken": refreshToken})
	}
}

// func (controller *UserController) RefreshTokensHandler() gin.HandlerFunc {
// 	return func(c *gin.Context) {
// 		refreshToken, err := c.Cookie("refreshToken")
// 		if err != nil {
// 			c.JSON(401, gin.H{"message": "Refresh Token not present"})
// 			return
// 		}

// 		claims, err := helpers.ValidateToken(refreshToken)
// 		if err != nil {
// 			controller.l.Error("Invalid Refresh Token")
// 			c.JSON(http.StatusUnauthorized, gin.H{"message": "invalid refresh token"})
// 			return
// 		}

// 		token, refreshToken, err := helper.GenerateTokens(user)
// 		if err != nil {
// 			controller.l.Error(err.Error())
// 			c.JSON(http.StatusUnauthorized, gin.H{"message": err.Error()})
// 			return
// 		}
// 		helper.SetCookies(c, token, refreshToken)
// 		c.JSON(http.StatusOK, gin.H{"message": "success"})
// 	}
// }
