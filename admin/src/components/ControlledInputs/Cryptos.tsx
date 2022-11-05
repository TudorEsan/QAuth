import { Divider, Grid, IconButton } from "@mui/material";
import {
  Control,
  FieldArrayWithId,
  UseFieldArrayRemove,
} from "react-hook-form";
import { IRecordForm } from "../../types/record";
import { ControlledTextField } from "./ControlledTextField";
import { Add, Remove } from "@mui/icons-material";

interface IRecordCryptosProps {
  fields: FieldArrayWithId<IRecordForm, "cryptos", "id">[];
  control: Control<IRecordForm, object>;
  remove: UseFieldArrayRemove;
}

export const Cryptos = ({ control, fields, remove }: IRecordCryptosProps) => {
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
                  name={`cryptos.${index}.symbol`}
                  label="Symbol"
                />
              </Grid>
              <Grid item md={6} lg={4} xs={12}>
                <ControlledTextField
                  key={field.id}
                  control={control}
                  type="number"
                  name={`cryptos.${index}.coins`}
                  label="Coins"
                />
              </Grid>
              <Grid item md={6} lg={4} xs={12}>
                <ControlledTextField
                  key={field.id}
                  type="number"
                  control={control}
                  name={`cryptos.${index}.currentPrice`}
                  label="Current Price"
                />
              </Grid>
            </Grid>
            <Grid item md={1} xs={12} textAlign="center">
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
