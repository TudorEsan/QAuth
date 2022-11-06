import React, { useEffect, useState } from "react";
import { handleError, handleSuccess } from "../helpers/state";
import { getUsers } from "../service/AuthService";
import { getReservation } from "../service/ReservationService";
import { getRooms } from "../service/RoomsService";
import { IAccount } from "../types/account";
import { IRequestState } from "../types/general";
import { IReservation } from "../types/reservatoion";

export const useReservation = () => {
  const [reservations, setReservations] = useState<
    IRequestState<IReservation[]>
  >({
    data: [],
    loading: true,
    error: null,
  });

  const initRooms = async () => {
    try {
      const reservations = await getReservation();
      console.log(reservations);
      handleSuccess(reservations, setReservations);
    } catch (e) {
      handleError(setReservations, "Could not set reservation");
    }
  };

  const refresh = () => {
    initRooms();
  };

  useEffect(() => {
    initRooms();
  }, []);

  return {
    reservations,
    refresh,
  };
};
