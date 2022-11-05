interface LoginInput {
  email: string;
  password: string;
}

interface RegisterInput {
  email: string;
  username: string;
  password: string;
  confirmPassword: string;
}

interface IUser {
  id: string;
  email: string;
  username: string;
  emailValidated: boolean;
  token: string;
}

export interface IClaims {
  Id: string;
  Email: string;
  Username: string;
  EmailValidated: boolean;
}
