import axios from "../axiosConfig";
import { IRecord, IRecordForm } from "../types/record";
import { serverUrl } from "./general";

export const addRecordReq = async (record: IRecordForm) => {
  console.log(record.date);
  return axios.post("/records", record);
};

export const getRecordsReq = async (
  page = 0,
  pageSize = 0
): Promise<IRecord[]> => {
  const resp = await axios.get(
    serverUrl() + `/records?page=${page}&pageSize=${pageSize}`
  );
  return resp.data.records as IRecord[];
};

export const getRecordReq = async (id: string): Promise<IRecord> => {
  const resp = await axios.get(serverUrl() + `/records/${id}`);
  return resp.data.record as IRecord;
};

export const deleteRecordReq = async (id: string) => {
  return axios.delete(serverUrl() + `/records/${id}`);
};

export const updateRecordReq = async (id: string, data: IRecordForm) => {
  return axios.put(serverUrl() + `/records/${id}`, data);
};

export const getRecordCountReq = async (): Promise<number> => {
  const resp = await axios.get(serverUrl() + "/records/count");
  return resp.data.recordCount as number;
};
