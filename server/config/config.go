package config

import (
	"os"

	"github.com/hashicorp/go-hclog"
	"github.com/joho/godotenv"
)

var l = hclog.Default().Named("Config")

func verifyAllEnvVars() {
	envVars := []string{"MONGO_URL", "JWT_SECRET", "SENDGRID_API_KEY", "DOMAIN_NAME"}
	for _, envVar := range envVars {
		if os.Getenv(envVar) == "" {
			l.Error(("Missing env var: " + envVar))
		}
	}
}

func init() {
	godotenv.Load(".env")
	verifyAllEnvVars()
}

type Config struct {
	MongoUrl     string
	JwtSecret    []byte
	SmtpUsername string
	SmtpPassword string
	DomainName   string
}

func getConfig() *Config {
	jwtSecret := []byte("secret")
	mongoUrl := "mongodb://localhost:27017"

	if os.Getenv("TESTING") != "" {
		goto ReturnConfig
	}

	if os.Getenv("JWT_SECRET") != "" {
		jwtSecret = []byte(os.Getenv("JWT_SECRET"))
	}
	
	if os.Getenv("MONGO_URL") != "" {
		mongoUrl = os.Getenv("MONGO_URL")
	}

ReturnConfig:

	return &Config{
		MongoUrl:     mongoUrl,
		JwtSecret:    jwtSecret,
		SmtpUsername: os.Getenv("SMTP_USERNAME"),
		SmtpPassword: os.Getenv("SMTP_PASSWORD"),
		DomainName:   os.Getenv("DOMAIN_NAME"),
	}
}

func New() *Config {
	return getConfig()
}
