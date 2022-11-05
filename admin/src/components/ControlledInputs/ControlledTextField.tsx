import { TextField } from "@mui/material";
import { useRef } from "react";
import { Controller } from "react-hook-form";

interface ControlledTextFieldProps {
  name: string;
  label: string;
  defaultValue?: any;
  control: any;
  rules?: any;
  disabled?: boolean;
  autofill?: string;
  type?: string;
}

export function ControlledTextField({
  control,
  name,
  label,
  defaultValue,
  rules,
  type,
  autofill,
  ...rest
}: ControlledTextFieldProps) {
  const textFieldRef = useRef<any>(null);

  return (
    <Controller
      control={control}
      name={name}
      defaultValue={defaultValue || (type === "number" ? 0 : "")}
      rules={rules}
      render={({ field: { onChange, value }, fieldState: { error } }) => {
        return (
          <>
            <TextField
              fullWidth
              type={type}
              variant="outlined"
              autoComplete={autofill || "off"}
              label={label}
              value={value || ""}
              onInput={ (e: any) => onChange(e.target.value) }
              onChange={(val) => {
                // val.target.value =
                //   type === "number"
                //     ? parseInt(val.target.value)
                //     : val.target.value;
                onChange(val);
              }}
              error={!!error}
              helperText={error?.message}

              ref={textFieldRef}
              {...rest}
            />
          </>
        );
      }}
    />
  );
}
ControlledTextField.defaultProps = {
  defaultValue: "",
};
