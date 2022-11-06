export const serverUrl = () => {
  return process.env.NODE_ENV === "development"
    ? "https://financeapp.tudoresan.ro"
    : "https://financeapp.tudoresan.ro";
};
