export const serverUrl = () => {
  return process.env.NODE_ENV === "development" ? "http://127.0.0.1:8081" : "https://financeapp.tudoresan.ro";
};
