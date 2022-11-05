package models

import "github.com/golang-jwt/jwt"

type SignedDetails struct {
	Email    string `json:"email"`
	Username string `json:"username"`
	Id       string `json:"id"`
	Role     int    `json:"role"`
	jwt.StandardClaims
}
