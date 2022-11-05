package helpers

import (
	"errors"
	"strings"
	"time"

	"github.com/TudorEsan/QPerior-Hackhaton/config"
	"github.com/TudorEsan/QPerior-Hackhaton/customErrors"
	"github.com/TudorEsan/QPerior-Hackhaton/models"
	"github.com/gin-gonic/gin"
	"github.com/golang-jwt/jwt"
)

func GenerateTokens(user models.User) (string, string, error) {
	conf := config.New()
	claims := &models.SignedDetails{
		Username: user.Name,
		Id:       user.ID.Hex(),
		Role:     user.Role,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Local().Add(time.Minute * 60 * 24 * 30).Unix(),
		},
	}

	refreshClaims := &models.SignedDetails{
		Id:       user.ID.Hex(),
		Username: user.Name,
		Role:     user.Role,
		StandardClaims: jwt.StandardClaims{
			ExpiresAt: time.Now().Local().Add(time.Hour * 24 * 30).Unix(),
		},
	}

	token, err := jwt.NewWithClaims(jwt.SigningMethodHS256, claims).SignedString([]byte(conf.JwtSecret))
	if err != nil {
		return "", "", err
	}
	refreshToken, err := jwt.NewWithClaims(jwt.SigningMethodHS256, refreshClaims).SignedString([]byte(conf.JwtSecret))
	if err != nil {
		return "", "", err
	}
	return token, refreshToken, nil
}

func ValidateToken(signedToken string) (*models.SignedDetails, error) {
	conf := config.New()
	token, err := jwt.ParseWithClaims(signedToken, &models.SignedDetails{}, func(token *jwt.Token) (interface{}, error) {
		return []byte(conf.JwtSecret), nil
	})
	if err != nil && strings.Contains(err.Error(), "expired") {
		return nil, customErrors.ExpiredToken{}
	}
	if err != nil {
		return nil, customErrors.InvalidToken{}
	}

	claims, ok := token.Claims.(*models.SignedDetails)
	if !ok {
		return nil, customErrors.InvalidToken{}
	}

	return claims, nil
}

func ValidateRole(c *gin.Context, desiredRole int) error {
	claims := c.MustGet("claims").(*models.SignedDetails)
	if claims.Role > desiredRole {
		c.AbortWithStatusJSON(401, gin.H{"message": "Unauthorized"})
		return errors.New("unauthorized")
	}
	return nil
}
