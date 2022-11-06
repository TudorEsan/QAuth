export const serverUrl = () => {
  return process.env.NODE_ENV === "development"
    ? "http://localhost:8080"
    : "https://financeapp.tudoresan.ro";
};
