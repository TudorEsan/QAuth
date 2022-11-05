function padTo2Digits(num: number) {
  return num.toString().padStart(2, "0");
}

export function formatDate(date: Date | null) {
  if (!date) {
    return "";
  }
  if (typeof date === "string") {
    date = new Date(date);
  }
  return [
    padTo2Digits(date.getDate()),
    padTo2Digits(date.getMonth() + 1),
    date.getFullYear(),
  ].join("-");
}

export const getUtcIso = (date: any) => {
  if (!date) {
    const date = new Date();
    date.setUTCHours(0, 0, 0, 0);
    return date.toISOString();
  }
  if (typeof date === "string") {
    date = new Date(date);
  }
  const newDate = date;
  newDate.setUTCHours(0, 0, 0, 0);
  return newDate.toISOString();
};
