package models

import "go.mongodb.org/mongo-driver/bson/primitive"

type Room struct {
	Id          primitive.ObjectID `json:"id" bson:"_id,omitempty"`
	Name        string             `json:"name" bson:"name" binding:"required"`
	MinimalRole int                `json:"minimalRole" bson:"minimalRole" binding:"required,gte=0"`
}

func (r Room) WithId() Room {
	return Room{
		Id:          primitive.NewObjectID(),
		Name:        r.Name,
		MinimalRole: r.MinimalRole,
	}
}

func NewRoom(name string, minimalRole int) Room {
	return Room{
		Id:          primitive.NewObjectID(),
		Name:        name,
		MinimalRole: minimalRole,
	}
}
