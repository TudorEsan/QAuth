import React, { useEffect, useState } from "react";
import { handleError, handleSuccess } from "../helpers/state";
import { getUsers } from "../service/AuthService";
import { getRooms } from "../service/RoomsService";
import { IAccount } from "../types/account";
import { IRequestState } from "../types/general";

export const useRooms = () => {
  const [rooms, setRooms] = useState<IRequestState<IRoom[]>>({
    data: [],
    loading: true,
    error: null,
  });

  const initRooms = async () => {
    try {
      const rooms = await getRooms();
      console.log(rooms);
      handleSuccess(rooms, setRooms);
    } catch (e) {
      handleError(setRooms, "Could not set rooms");
    }
  };

  const refresh = () => {
    initRooms();
  };

  useEffect(() => {
    initRooms();
  }, []);

  return {
    rooms,
    refresh,
  };
};
