import React from "react";
import { Route, Routes } from "react-router-dom";
import { Login } from "../pages";
import { Accounts } from "../pages/Accounts";
import { Home } from "../pages/Home";
import { Register } from "../pages/Register";
import { ProtectedRoute } from "./ProtectedRoute";

export const AppRoutes = () => {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <Home />
          </ProtectedRoute>
        }
      />
      <Route
        path="/accounts"
        element={
          <ProtectedRoute>
            <Accounts />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
};
