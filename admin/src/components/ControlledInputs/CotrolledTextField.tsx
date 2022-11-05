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
  type?: string;
}

export function ControlledTextField({
  control,
  name,
  label,
  defaultValue,
  rules,
  type,
  ...rest
}: ControlledTextFieldProps) {
  const textFieldRef = useRef<any>(null);

  return (
    <Controller
      control={control}
      name={name}
      defaultValue={defaultValue}
      rules={rules}
      render={({ field: { onChange, value }, fieldState: { error } }) => {
        return (
          <>
            <TextField
              fullWidth
              type={type}
              variant="outlined"
              label={label}
              value={value || ""}
              onChange={onChange}
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
