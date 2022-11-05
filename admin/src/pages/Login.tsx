import { LoadingButton } from "@mui/lab";
import {
  Box,
  Button,
  Card,
  CardContent,
  Link,
  TextField,
  Typography,
} from "@mui/material";
import React from "react";
import { useForm } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import { ControlledTextField } from "../components/ControlledInputs/ControlledTextField";
import { useAuth } from "../hooks/useAuth";
import { LoginInput } from "../types/auth";

export const Login = () => {
  const navigate = useNavigate();
  const { error, isLoading, login } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<LoginInput>({
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const handleLogin = (data: LoginInput) => {
    data.email = data.email.trim().toLowerCase();
    login(data.email, data.password);
  };

  return (
    <Box
      p={2}
      sx={{
        maxWidth: "600px",
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        width: "100%",
      }}
    >
      <Card>
        <form onSubmit={handleSubmit(handleLogin)}>
          <CardContent
            sx={{
              display: "flex",
              gap: 2,
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" textAlign="center">
              Log in!
            </Typography>
            <ControlledTextField
              name="email"
              label="Email"
              autofill="email"
              control={control}
              rules={{ required: true }}
            />
            <ControlledTextField
              name="password"
              label="Password"
              control={control}
              autofill="current-password"
              rules={{ required: true }}
              type="password"
            />
            <LoadingButton
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
              loading={isLoading}
            >
              Log in
            </LoadingButton>
            <Link
              component="button"
              underline="hover"
              onClick={() => navigate("../register")}
            >
              Register
            </Link>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};
