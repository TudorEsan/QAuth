import plainAxios from "axios";
import { deleteAllCookies } from "./helpers/authHelper";

const axios = plainAxios.create({
  baseURL: "/api",
  withCredentials: true,
  timeout: 20000,
});

axios.interceptors.request.use(
  (req) => {
    const token = window.localStorage.getItem("token");
    if (token !== null) {
      req.headers!["token"] = token;
    }
    return req;
  },
  (error) => {
    return Promise.reject(error);
  }
);

axios.interceptors.response.use(
  (response) => {
    return response;
  },
  (error) => {
    console.log(error);
    if (error.response.status === 401) {
      deleteAllCookies();
      window.location.reload();
    }
    return Promise.reject(error);
  }
);

export default axios;
