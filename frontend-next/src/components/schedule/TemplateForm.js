"use client";

import toast from "react-hot-toast";
import { ScheduleTemplateSchema } from "@/lib/zod/schemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useFieldArray, useForm } from "react-hook-form";
import { useState } from "react";

import {
  createScheduleTemplate,
  updateScheduleTemplate,
} from "@/services/client/schedule";
import { useAuth } from "@/contexts/AuthContext";
import { SheetClose, SheetFooter } from "@/components/shadcn/sheet";
import FormInput from "@/components/ui/FormInput";
import FormLabel from "@/components/ui/FormLabel";
import Button from "@/components/ui/Button";
import Checkbox from "@/components/ui/Checkbox";
import TemplateTimeInterval from "./TemplateTimeInterval";
import ErrorMessage from "@/components/ui/ErrorMessage";
import SpinnerMini from "@/components/ui/SpinnerMini";
import { DAYS_OF_WEEK } from "@/constants/schedules";
import { stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { useRouter } from "next/navigation";

function TemplateForm({ templateToEdit = {}, closeSheet }) {
  const router = useRouter();
  const { user } = useAuth();
  const { name, slots, id: templateId } = templateToEdit;
  const isEdit = Boolean(templateId);
  const {
    register,
    control,
    handleSubmit,
    setValue,
    getValues,
    trigger,
    setError,
    reset,
    formState: { errors, isSubmitting, isSubmitted },
  } = useForm({
    resolver: zodResolver(ScheduleTemplateSchema),
    defaultValues: isEdit
      ? {
          name,
          slots: slots.map((slot) => ({
            weekDay: slot.weekDay,
            startTime: stripSeconds(slot.startTime),
            endTime: stripSeconds(slot.endTime),
          })),
        }
      : { name: "", slots: [] },
    reValidateMode: "onChange",
  });

  const { fields, insert, append, remove, replace } = useFieldArray({
    control,
    name: "slots",
  });

  const [applyToAll, setApplyToAll] = useState(false);

  const fieldsToShow = applyToAll && fields.length > 0 ? [fields[0]] : fields;
  const slotsError = errors?.slots?.root?.message || errors?.slots?.message;

  const handleApplyAll = (index, field, value) =>
    fields.forEach((_, i) => {
      if (i !== index) {
        setValue(`slots.${i}.${field}`, value);
      }
    });

  async function handleToggle(dayNum) {
    const selectedIndex = fields.findIndex((field) => field.weekDay === dayNum);

    if (selectedIndex >= 0) remove(selectedIndex);
    else {
      const newSlot = { weekDay: dayNum, startTime: "09:00", endTime: "17:00" };

      // Find the first day in the array that is ahead of the toggled day
      const insertIndex = fields.findIndex((f) => f.weekDay > dayNum);

      if (insertIndex >= 0) {
        // Safely inject it at the correct index to keep everything sorted
        insert(insertIndex, newSlot);
      } else {
        // If it's larger than all existing days, just append it to the end
        append(newSlot);
      }
    }

    if (isSubmitted) await trigger("slots");
  }

  async function onSubmit(data) {
    setError("root", { message: null });
    try {
      if (isEdit) await updateScheduleTemplate(data, user?.sub, templateId);
      else await createScheduleTemplate(data, user?.sub);

      closeSheet?.();
      router.refresh();
      toast.success(`Template ${isEdit ? "Updated" : "Created"} successfully`);
    } catch (err) {
      console.error(err);
      setError("root", { message: "Something went wrong, Try again" });
    }
  }

  return (
    <form
      onSubmit={handleSubmit(onSubmit)}
      className="flex h-full flex-col overflow-hidden"
    >
      {/* Form Scrollable Content */}
      <div className="no-scrollbar flex-1 overflow-y-auto py-4">
        <div className="space-y-6 p-4">
          <FormInput
            label="Template Name"
            placeholder="e.g. Regular Schedule"
            required
            {...register("name")}
            error={errors?.name?.message}
            disabled={isSubmitting}
          />

          {/* Days of week */}
          <div>
            <FormLabel label="Working Days" required />
            <div className="flex flex-wrap gap-2 overflow-auto">
              {DAYS_OF_WEEK.map((day, index) => {
                const isSelected = fields.some((f) => f.weekDay === index);
                return (
                  <button
                    key={day}
                    type="button"
                    className={`${isSelected ? "border-primary bg-primary/10 text-primary" : "bg-surface-overlay/75 hover:bg-surface-overlay border-border text-text-muted"} min-w-[50px] flex-1 cursor-pointer rounded-md border py-2 text-center transition-colors`}
                    onClick={() => handleToggle(index)}
                    disabled={isSubmitting}
                  >
                    {day}
                  </button>
                );
              })}
            </div>
            <p
              className={`mt-2 text-xs ${slotsError ? "text-danger" : "text-text-muted"}`}
            >
              Click to select working days
            </p>
          </div>

          <hr className="border-border" />

          {/* Generated Time for each day */}
          {fieldsToShow.length > 0 && (
            <div>
              <FormLabel label="Working Hours" />
              <Checkbox
                label="Apply same hours to all selected days"
                checked={applyToAll}
                onChange={(e) => {
                  const val = e.target.checked;
                  if (val) {
                    handleApplyAll(
                      0,
                      "startTime",
                      getValues("slots.0.startTime"),
                    );
                    handleApplyAll(0, "endTime", getValues("slots.0.endTime"));
                    if (isSubmitted) trigger("slots");
                  }
                  setApplyToAll(val);
                }}
                disabled={isSubmitting}
              />
              <div className="mt-5 space-y-5">
                {fieldsToShow.map((f, index) => (
                  <TemplateTimeInterval
                    key={f.id}
                    day={applyToAll ? "All Selected" : DAYS_OF_WEEK[f.weekDay]}
                    startInput={
                      <FormInput
                        type="time"
                        {...register(`slots.${index}.startTime`, {
                          onChange: (val) => {
                            if (applyToAll)
                              handleApplyAll(0, "startTime", val.target.value);
                            if (isSubmitted) trigger("slots");
                          },
                        })}
                        error={errors?.slots?.[index]?.startTime?.message}
                        disabled={isSubmitting}
                      />
                    }
                    endInput={
                      <FormInput
                        type="time"
                        {...register(`slots.${index}.endTime`, {
                          onChange: (val) => {
                            if (applyToAll)
                              handleApplyAll(0, "endTime", val.target.value);
                            if (isSubmitted) trigger("slots");
                          },
                        })}
                        error={errors?.slots?.[index]?.endTime?.message}
                        disabled={isSubmitting}
                      />
                    }
                    onDelete={() => {
                      if (applyToAll) {
                        replace([]);
                        setApplyToAll(false);
                      } else remove(index);
                    }}
                  />
                ))}
              </div>
            </div>
          )}
        </div>
      </div>

      <SheetFooter className="border-border shrink-0 border-t pt-4">
        <div>
          <Button className="w-full" type="submit" disabled={isSubmitting}>
            {isSubmitting ? (
              <SpinnerMini />
            ) : isEdit ? (
              "Edit Template"
            ) : (
              "Create Template"
            )}
          </Button>
          {errors?.root?.message && (
            <ErrorMessage
              message={errors?.root?.message}
              className="text-center"
            />
          )}
        </div>

        <Button
          variation="ghost"
          type="button"
          className="border border-slate-300"
          onClick={isEdit ? () => reset({ name, slots }) : closeSheet}
          disabled={isSubmitting}
        >
          {isEdit ? "Reset" : "Cancel"}
        </Button>
      </SheetFooter>
    </form>
  );
}

export default TemplateForm;
