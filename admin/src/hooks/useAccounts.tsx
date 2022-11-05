import React, { useEffect, useState } from "react";
import { handleError, handleSuccess } from "../helpers/state";
import { getUsers } from "../service/AuthService";
import { IAccount } from "../types/account";
import { IRequestState } from "../types/general";

export const useAccounts = () => {
  const [accounts, setAccounts] = useState<IRequestState<IAccount[]>>({
    data: [],
    loading: true,
    error: null,
  });


  const initAccounts = async () => {
    try {
      const users = await getUsers();
      console.log(users)
      handleSuccess(users, setAccounts);
    } catch (e) {
      handleError(setAccounts, "Could not load accounts");
    }
  };

  useEffect(() => {
    initAccounts();
  }, [])

  return {
    accounts
  }

};
