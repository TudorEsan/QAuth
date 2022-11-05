export const deepCopy = (obj: any) => {
  return JSON.parse(JSON.stringify(obj));
};

export const round = (value: number, decimals: number = 2): number => {
  return Math.floor(value * Math.pow(10, decimals)) / Math.pow(10, decimals);
};
