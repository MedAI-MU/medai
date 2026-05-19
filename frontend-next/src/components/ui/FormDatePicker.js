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

function FormDatePicker({
  name,
  control,
  label,
  placeholder = "Select date",
  rules,
  disabled,
  disabledRange = null,
}) {
  const [openPopover, setOpenPopover] = useState(false);

  return (
    <Controller
      name={name}
      control={control}
      rules={rules}
      render={({ field, fieldState }) => (
        <div className="flex flex-col gap-2">
          {label && <label>{label}</label>}

          <Popover open={openPopover} onOpenChange={setOpenPopover}>
            <PopoverTrigger asChild>
              <button
                type="button"
                className={`border-border bg-surface disabled:bg-surface-overlay disabled:text-text-subtle flex w-full justify-start rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2 disabled:cursor-not-allowed disabled:opacity-70 ${
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
                captionLayout="dropdown"
                selected={field.value}
                onSelect={(date) => {
                  field.onChange(date);
                  setOpenPopover(false);
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
    />
  );
}

export default FormDatePicker;
