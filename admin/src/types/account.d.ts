export interface IAccount {
  id: string;
  name: string;
  email: string;
  role: int;
}

export interface IAccountForm {
  name: string;
  email: string;
  password: string;
  role: number;
}
