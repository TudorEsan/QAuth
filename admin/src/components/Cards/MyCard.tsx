import { Card, CardActionArea, CardContent } from "@mui/material";
import React from "react";

interface IMyCardProps {
  children: React.ReactNode;
  [x: string]: any;
}

export const MyCard = ({ children, rest }: IMyCardProps) => {
  return (
    <Card elevation={7} sx={{ height: "100%" }} {...rest}>
      <CardContent sx={{ height: "100%" }}>{children}</CardContent>
    </Card>
  );
};
