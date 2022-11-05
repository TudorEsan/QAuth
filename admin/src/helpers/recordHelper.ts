import { IRecord, IRecordForm } from "../types/record";
import { deepCopy, round } from "./generalHelpers";

const roundPercent = (value: number, decimals: number) => {
  return (
    Math.floor(value * Math.pow(10, decimals)) / Math.pow(10, decimals - 2)
  );
};

export const getTotalNetWorth = (record?: IRecord | null) => {
  if (!record) {
    return 0;
  }
  return record.liquidity + record.investedAmount;
};

export const getRecordForRequest = (record: IRecordForm) => {
  const newRecord = deepCopy(record) as IRecordForm;

  newRecord.stocks = newRecord.stocks.map((stock) => {
    return {
      ...stock,
      valuedAt: stock.currentPrice * stock.shares,
    };
  });

  newRecord.cryptos = newRecord.cryptos.map((crypto) => {
    return {
      ...crypto,
      valuedAt: crypto.currentPrice * crypto.coins,
    };
  });

  return newRecord;
};

export const calculateRecordWithCurrentPrices = (record: IRecord): IRecord => {
  const newRecord = deepCopy(record) as IRecord;

  newRecord.stocks = newRecord.stocks.map((stock) => {
    return {
      ...stock,
      currentPrice: round(stock.valuedAt! / stock.shares, 4),
    };
  });

  newRecord.cryptos = newRecord.cryptos.map((crypto) => {
    return {
      ...crypto,
      currentPrice: round(crypto.valuedAt! / crypto.coins, 4),
    };
  });

  return newRecord;
};

export const getDiversification = (record: IRecord) => {
  const diversification = [];
  const total = record.stocksValue + record.cryptosValue + record.liquidity;
  diversification.push({
    symbol: "Liquidity",
    percent: roundPercent(record.liquidity / total, 3),
  });
  diversification.push({
    symbol: "Stocks",
    percent: roundPercent(record.stocksValue / total, 3),
  });
  diversification.push({
    symbol: "Cryptos",
    percent: roundPercent(record.cryptosValue / total, 3),
  });
  console.log(diversification);
  return diversification;
};
