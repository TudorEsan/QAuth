package helpers

import (
	"context"
	"fmt"
	"time"

	"github.com/TudorEsan/QPerior-Hackhaton/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
)

func GetReservationsFromDate(reservationCol *mongo.Collection, year, month, day int) ([]models.Reservation, error) {
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	// get the reservations from the database
	reservations := []models.Reservation{}
	cursor, err := reservationCol.Aggregate(ctx,
		bson.A{
			bson.D{
				{"$addFields",
					bson.D{
						{"year", bson.D{{"$year", "$from"}}},
						{"month", bson.D{{"$month", "$from"}}},
						{"day", bson.D{{"$dayOfMonth", "$from"}}},
					},
				},
			},
			bson.D{
				{"$match",
					bson.D{
						{"year", year},
						{"month", month},
						{"day", day},
					},
				},
			},
			bson.D{
				{"$unset",
					bson.A{
						"year",
						"month",
						"day",
					},
				},
			},
		},
	)
	if err != nil {
		return nil, err
	}
	for cursor.Next(ctx) {
		var reservation models.Reservation
		err := cursor.Decode(&reservation)
		if err != nil {
			fmt.Println(err)
			return nil, err
		}
		reservations = append(reservations, reservation)
	}
	return reservations, nil
}

func IsReservationValid(reservation models.Reservation, dayReservations []models.Reservation) bool {
	// check if the reservation is valid
	// if the reservation is valid, add it to the database
	// if the reservation is not valid, return an error

	for _, dayReservation := range dayReservations {
		fromDayRes := dayReservation.From
		toDayRes := dayReservation.From.Add(time.Minute * time.Duration(dayReservation.DurationMinutes))

		from := reservation.From
		to := reservation.From.Add(time.Minute * time.Duration(reservation.DurationMinutes))

		// check if the reservation is in the same time as another reservation
		if fromDayRes.Before(to) && from.Before(toDayRes) {
			return false
		}
	}
	return true

}
