"use client";

import { useState } from "react";
import { Controller } from "react-hook-form";
import { format } from "date-fns";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/shadcn/popover";
import { Calendar } from "@/components/shadcn/calendar";
import ErrorMessage from "@/components/ui/ErrorMessage";
import FormLabel from "./FormLabel";

function FormDatePicker({
  name,
  control,
  rules,
  label,
  placeholder = "Select date",
  required = false,
  disabled,
  disabledRange = null,
  ...props
}) {
  const [openPopover, setOpenPopover] = useState(false);

  return (
    <Controller
      name={name}
      control={control}
      {...(!!rules && { rules })}
      render={({ field, fieldState }) => (
        <div className="flex flex-col">
          {label && <FormLabel label={label} required={required} />}

          <Popover open={openPopover} onOpenChange={setOpenPopover}>
            <PopoverTrigger asChild>
              <button
                type="button"
                className={`border-border bg-surface-overlay disabled:text-text-subtle flex w-full justify-start rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2 disabled:cursor-not-allowed disabled:opacity-75 ${
                  !field.value ? "text-text-subtle" : "text-text-base"
                } ${fieldState.error ? "ring-danger border-danger ring-2" : "focus:ring-primary/90"}`}
                disabled={disabled}
              >
                {field.value ? format(field.value, "yyyy-MM-dd") : placeholder}
              </button>
            </PopoverTrigger>

            <PopoverContent className="w-auto p-0">
              <Calendar
                mode="single"
                // if required, disables deselecting.
                required={required}
                captionLayout="dropdown"
                defaultMonth={field.value ?? new Date()}
                selected={field.value}
                onSelect={(date) => {
                  if (date || !rules?.required) {
                    field.onChange(date);
                    setOpenPopover(false);
                  }
                }}
                disabled={disabledRange}
              />
            </PopoverContent>
          </Popover>

          {fieldState?.error && (
            <ErrorMessage message={fieldState.error?.message} />
          )}
        </div>
      )}
      {...props}
    />
  );
}

export default FormDatePicker;
