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
import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { RegisterInput } from "../types/auth";

const formSchema = Yup.object({
  username: Yup.string()
    .required("Username is required")
    .min(4, "Username must be at least 4 characters"),
  password: Yup.string()
    .required("Password is required")
    .min(8, "Password must be at least 8 characters"),
  confirmPassword: Yup.string()
    .oneOf([Yup.ref("password"), null], "Passwords must match")
    .required("Confirm Password is required")
    .min(8, "Password must be at least 8 characters"),
  email: Yup.string().email("Email is invalid").required("Email is required"),
}).required();

const formConfig = {
  resolver: yupResolver(formSchema),
};

export const Register = () => {
  const navigate = useNavigate();
  const { error, isLoading, register } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { errors },
  } = useForm<RegisterInput>(formConfig);

  const handleRegister = (data: RegisterInput) => {
    data.username = data.username.trim().toLowerCase();
    register(data);
  };


  return (
    <Box
      sx={{
        maxWidth: "600px",
        position: "absolute",
        top: "50%",
        left: "50%",
        transform: "translate(-50%, -50%)",
        width: "100%",
        boxSizing: "border-box",
        padding: "20px",
      }}
    >
      <Card>
        <form onSubmit={handleSubmit(handleRegister)}>
          <CardContent
            sx={{
              display: "flex",
              gap: 2,
              flexDirection: "column",
              alignItems: "center",
            }}
          >
            <Typography variant="h4" textAlign="center">
              Register
            </Typography>
            <ControlledTextField
              name="username"
              label="Username"
              control={control}
            />
            <ControlledTextField name="email" label="Email" control={control} />
            <ControlledTextField
              name="password"
              label="Password"
              control={control}
              type="password"
            />
            <ControlledTextField
              name="confirmPassword"
              label="Confirm Password"
              control={control}
              type="password"
            />
            <Button
              variant="contained"
              color="primary"
              sx={{ maxWidth: "200px" }}
              type="submit"
            >
              Register
            </Button>
            <Link
              component="button"
              underline="hover"
              onClick={() => navigate("../login")}
            >
              Login
            </Link>
            {error && <Typography color="error">{error}</Typography>}
          </CardContent>
        </form>
      </Card>
    </Box>
  );
};
