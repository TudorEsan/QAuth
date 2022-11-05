import * as Yup from "yup";

export const recordFormSchema = Yup.object({
  date: Yup.string().required("Date is required"),
  liquidity: Yup.number()
    .required("Liquidity is required")
    .min(0, "Liquidity must be greater or equal to 0"),
  stocks: Yup.array().of(
    Yup.object().shape({
      symbol: Yup.string().required("Symbol is required"),
      shares: Yup.number(),
      currentPrice: Yup.number()
        .required("Field is required")
        .min(0, "Price must be greater or equal to 0"),
    })
  ),
  cryptos: Yup.array().of(
    Yup.object().shape({
      symbol: Yup.string().required("Symbol is required"),
      coins: Yup.number(),
      currentPrice: Yup.number()
        .required("Field is required")
        .min(0, "Price must be greater or equal to 0"),
    })
  ),
});
