package models

import (
	"go.mongodb.org/mongo-driver/bson/primitive"
	"golang.org/x/crypto/bcrypt"
)

type User struct {
	ID        primitive.ObjectID `json:"id" bson:"_id,omitempty"`
	Name      string             `json:"name" bson:"name"`
	Email     string             `json:"email" bson:"email"`
	HashedPwd string             `json:"hashedPwd" bson:"hashedPwd"`
	Role      int                `json:"role" bson:"role"`
}

type UserSignupForm struct {
	Name     string `json:"name" binding:"required"`
	Email    string `json:"email" binding:"required"`
	Password string `json:"password" binding:"required"`
}

type UserLoginForm struct {
	Email    string `json:"email" binding:"required"`
	Password string `json:"password" binding:"required"`
}

func hashPassword(password string) (string, error) {
	bytes, err := bcrypt.GenerateFromPassword([]byte(password), 14)
	return string(bytes), err
}

func NewUser(user UserSignupForm) User {
	hashedPwd, _ := hashPassword(user.Password)
	return User{
		Name:      user.Name,
		Email:     user.Email,
		HashedPwd: hashedPwd,
		Role:      10000,
	}
}
