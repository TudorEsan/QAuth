package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Reservation struct {
	Id              primitive.ObjectID   `json:"id" bson:"_id,omitempty"`
	RoomId          primitive.ObjectID   `json:"roomId" bson:"roomId"`
	UserId          primitive.ObjectID   `json:"userId" bson:"userId"`
	From            time.Time            `json:"from" bson:"from"`
	DurationMinutes int                  `json:"durationMinutes" bson:"durationMinutes"`
	Subject         string               `json:"subject" bson:"subject"`
	Guests          []primitive.ObjectID `json:"guests" bson:"guests"`
}

type ReservationForm struct {
	RoomId          primitive.ObjectID `json:"roomId" binding:"required"`
	From            time.Time         	`json:"-"` 
	DurationMinutes int                `json:"durationMinutes" binding:"required"`
	Subject         string             `json:"subject" binding:"required"`
	Guests          []string           `json:"guests"`
}

func NewReservation(reservationForm ReservationForm) Reservation {
	guestIds := make([]primitive.ObjectID, len(reservationForm.Guests))
	for i, guest := range reservationForm.Guests {
		guestIds[i], _ = primitive.ObjectIDFromHex(guest)
	}
	return Reservation{
		Id:              primitive.NewObjectID(),
		RoomId:          reservationForm.RoomId,
		From:            reservationForm.From,
		DurationMinutes: reservationForm.DurationMinutes,
		Subject:         reservationForm.Subject,
		Guests:          guestIds,
	}
}
