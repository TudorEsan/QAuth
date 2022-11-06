import axios from "../axiosConfig";
import { IAccount, IAccountForm } from "../types/account";
import { IUser, RegisterInput } from "../types/auth";
import { serverUrl } from "./general";

export const signIn = async (email: string, password: string) => {
  return (await axios.post(serverUrl() + "/login", { email, password }))
    .data as IUser;
};

export const signUp = async (data: RegisterInput) => {
  return (await axios.post(serverUrl() + "/register", data)).data as IUser;
};

export const getUsers = async () => {
  return (await axios.get(serverUrl() + "/users")).data as IAccount[];
};

export const registerUser = async (user: IAccountForm) => {
  return (await axios.post(serverUrl() + "/register", user)).data as IUser;
};

export const deleteUser = async (id: string) => {
  return (await axios.delete(serverUrl() + "/users/" + id)).data as IUser;
};
