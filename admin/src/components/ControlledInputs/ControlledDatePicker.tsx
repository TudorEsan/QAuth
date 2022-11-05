import DateFnsUtils from "@date-io/date-fns";
import { Controller } from "react-hook-form";
import { TextField } from "@mui/material";
import { DatePicker, LocalizationProvider } from "@mui/x-date-pickers";
import { format } from "date-fns";
import { zonedTimeToUtc } from "date-fns-tz";
import { getUtcIso } from "../../helpers/date";

interface ControlledDatePickerProps {
  name: string;
  label: string;
  defaultValue?: any;
  control: any;
  rest?: any;
}

export function ControlledDatePicker({
  name,
  label,
  defaultValue,
  control,
  rest,
}: ControlledDatePickerProps) {
  return (
    <Controller
      control={control}
      name={name}
      defaultValue={getUtcIso(defaultValue)}
      render={({ field: { onChange, value } }) => (
        <LocalizationProvider dateAdapter={DateFnsUtils}>
          <DatePicker
            label={label}
            value={value}
            onChange={onChange}
            renderInput={(params: any) => (
              <TextField fullWidth sx={{ minWidth: 150 }} {...params} />
            )}
            inputFormat="dd/MM/yyyy"
          />
        </LocalizationProvider>
      )}
    />
  );
}
