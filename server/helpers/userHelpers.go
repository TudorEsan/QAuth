package helpers

import (
	"context"
	"errors"

	"github.com/TudorEsan/QPerior-Hackhaton/models"
	"github.com/gin-gonic/gin"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
	"golang.org/x/crypto/bcrypt"
)

func HashPassword(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	return string(bytes), err
}

func ValidEmail(ctx context.Context, userCollection *mongo.Collection, username string) error {
	count, err := userCollection.CountDocuments(ctx, bson.M{"email": username})
	if err != nil {
		return err
	}
	if count > 0 {
		return errors.New("username already exists")
	}
	return nil
}

func CheckPassword(dbUser models.User, user models.UserLoginForm) (err error) {
	err = bcrypt.CompareHashAndPassword([]byte(dbUser.HashedPwd), []byte(user.Password))
	if err != nil {
		return errors.New("credentials are not good")
	}
	return nil
}

func SetCookies(c *gin.Context, token string, refreshToken string) {
	c.SetCookie("token", token, 60*60*24*30, "", "", false, false)
	c.SetCookie("refreshToken", refreshToken, 60*60*24*30, "", "", false, false)
}
