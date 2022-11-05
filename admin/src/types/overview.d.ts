export interface INetWorth {
  date: Date;
  total: number;
}

export interface ILinear {
  x: any;
  y: any;
}

export interface ILiquidity {
  date: Date;
  liquidity: number;
}

export interface IOverview {
  liquidityOverview: ILiquidity[];
  networthOverview: INetWorth[];
  lastRecord?: IRecord;
  currentRecord?: IRecord;
}
