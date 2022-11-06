import axios from "../axiosConfig";
import { IReservation } from "../types/reservatoion";
import { serverUrl } from "./general";

export const getReservation = async () => {
  const res = (await axios.get(serverUrl() + "/allReservations")).data;
  console.log(res);
  return res as IReservation[];
};

export const addReservation = async (res: IReservation) => {
  return (await axios.post(serverUrl() + "/reservation/" + res.from, res)).data;
};

export const deleteReservation = async (id: string) => {
  return (await axios.delete(serverUrl() + "/reservation/" + id)).data as IRoom;
};
