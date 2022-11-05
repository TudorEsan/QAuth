package database

import (
	"context"
	"time"

	"github.com/TudorEsan/QPerior-Hackhaton/config"
	"github.com/hashicorp/go-hclog"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

var l = hclog.Default()

func DbInstace() *mongo.Client {
	url := config.New().MongoUrl
	client, err := mongo.NewClient(options.Client().ApplyURI(url))
	if err != nil {
		l.Error("Failed to create new client", "error", err)
		panic(err)
	}
	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()
	err = client.Connect(ctx)
	if err != nil {
		l.Error("Could not connect to database", err)
		panic(err)
	}
	l.Info("Db Connected")
	return client
}

func OpenCollection(client *mongo.Client, collenctionName string) *mongo.Collection {
	collection := client.Database("q-perior").Collection(collenctionName)
	return collection
}
