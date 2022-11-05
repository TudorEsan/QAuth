import { Divider, Grid, IconButton } from "@mui/material";
import { Control, FieldArrayWithId, UseFieldArrayRemove } from "react-hook-form";
import { IRecordForm } from "../../types/record";
import { ControlledTextField } from "./ControlledTextField";
import { Add, Remove } from "@mui/icons-material";

interface IRecordStocksProps {
  fields: FieldArrayWithId<IRecordForm, "stocks", "id">[];
  control: Control<IRecordForm, object>;
  remove: UseFieldArrayRemove;
}

export const Stocks = ({ control, fields, remove }: IRecordStocksProps) => {
  return (
    <>
      {fields.map((field, index) => (
        <div key={field.id}>
          <Grid
            container
            spacing={2}
            display="flex"
            justifyContent="center"
            alignItems="center"
          >
            <Grid item container md={11} spacing={2} xs={12}>
              <Grid item md={12} lg={4} xs={12}>
                <ControlledTextField
                  key={field.id}
                  control={control}
                  name={`stocks.${index}.symbol`}
                  label="Symbol"
                />
              </Grid>
              <Grid item md={6} lg={4} xs={12}>
                <ControlledTextField
                  key={field.id}
                  control={control}
                  type="number"
                  name={`stocks.${index}.shares`}
                  label="Shares"
                />
              </Grid>
              <Grid item md={6} lg={4} xs={12}>
                <ControlledTextField
                  key={field.id}
                  type="number"
                  control={control}
                  name={`stocks.${index}.currentPrice`}
                  label="Current Price"
                />
              </Grid>
            </Grid>
            <Grid item md={1} xs={12} textAlign="center" key={field.id + "1"}>
              <IconButton onClick={() => remove(index)}>
                <Remove sx={{ margin: "0 auto" }} />
              </IconButton>
            </Grid>
          </Grid>
          <Divider sx={{ marginBottom: 2, mt: 2 }} />
        </div>
      ))}
    </>
  );
};