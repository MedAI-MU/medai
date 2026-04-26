import { Controller } from "react-hook-form";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/shadcn/popover";
import { Calendar } from "@/components/shadcn/calendar";
import { format } from "date-fns";
import ErrorMessage from "./ErrorMessage";

function FormDatePicker({
  name,
  control,
  label,
  rules,
  disabled,
  disabledRange = null,
}) {
  return (
    <Controller
      name={name}
      control={control}
      rules={rules}
      render={({ field, fieldState }) => (
        <div className="flex flex-col gap-2">
          {label && <label>{label}</label>}

          <Popover>
            <PopoverTrigger asChild>
              <button
                type="button"
                className={`border-border bg-surface focus:ring-primary/90 disabled:bg-surface-overlay disabled:text-text-subtle flex w-full justify-start rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2 disabled:cursor-not-allowed disabled:opacity-70 ${
                  !field.value ? "text-text-subtle" : "text-text-base"
                } ${fieldState.error ? "ring-danger border-danger ring-2" : ""}`}
                disabled={disabled}
              >
                {field.value
                  ? format(field.value, "yyyy-MM-dd")
                  : "Select date"}
              </button>
            </PopoverTrigger>

            <PopoverContent className="w-auto p-0">
              <Calendar
                mode="single"
                captionLayout="dropdown"
                selected={field.value}
                onSelect={field.onChange}
                disabled={disabledRange}
              />
            </PopoverContent>
          </Popover>

          {fieldState?.error && <ErrorMessage message={fieldState.error} />}
        </div>
      )}
    />
  );
}

export default FormDatePicker;
