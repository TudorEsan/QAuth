import React from "react";
import axios from "../axiosConfig";
import { INetWorth, IOverview } from "../types/overview";
import { serverUrl } from "./general";

export const getNetWorthOverviewReq = async (limit: number) => {
  const resp = await axios.get(serverUrl() + `/overview/networth`, {
    params: { limit },
  });
  return resp.data.overview as IOverview;
};
