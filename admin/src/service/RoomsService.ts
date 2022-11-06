import axios from "../axiosConfig";
import { serverUrl } from "./general";

export const getRooms = async () => {
  const res = (await axios.get(serverUrl() + "/rooms")).data;
  console.log(res)
  return res as IRoom[];
};

export const addRoom = async (room: IRoomForm) => {
  return (await axios.post(serverUrl() + "/room", room)).data.rooms as IRoom[];
};

export const deleteRoom = async (id: string) => {
  return (await axios.delete(serverUrl() + "/room/" + id)).data as IRoom;
};
