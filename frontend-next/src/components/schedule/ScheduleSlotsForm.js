"use client";

import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import { CirclePlus } from "lucide-react";
import { useFieldArray, useForm } from "react-hook-form";
import { ScheduleSlotsSchema } from "@/lib/zod/scheduleSchemas";
import { zodResolver } from "@hookform/resolvers/zod";
import { useDoctorInfo } from "@/contexts/DoctorInfoContext";
import { formatDate } from "@/lib/utils/DateTimeHelpers";

import { SheetBody, SheetFooter } from "@/components/shadcn/sheet";
import Button from "@/components/ui/Button";
import SheetForm from "@/components/ui/SheetForm";
import SpinnerMini from "@/components/ui/SpinnerMini";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { createScheduleSlots } from "@/services/client/schedule";
import ScheduleSlotsDayItem from "./ScheduleSlotsDayItem";

const getDefaultValue = () => ({
  days: [
    {
      date: new Date(),
      slots: [{ startTime: "09:00", endTime: "10:00" }],
    },
  ],
});

function ScheduleSlotsForm({ closeSheet }) {
  const {
    doctor: { doctorId },
  } = useDoctorInfo();

  const {
    register,
    control,
    handleSubmit,
    getValues,
    trigger,
    reset,
    clearErrors,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    resolver: zodResolver(ScheduleSlotsSchema),
    defaultValues: getDefaultValue(),
  });
  const {
    fields: days,
    append: appendDay,
    remove: removeDay,
  } = useFieldArray({ control, name: "days" });
  const router = useRouter();

  function handleAddDay() {
    const currentDays = getValues(`days`);
    const lastDate = currentDays[currentDays?.length - 1]?.date;

    if (!lastDate) {
      appendDay({
        date: new Date(),
        slots: [{ startTime: "09:00", endTime: "10:00" }],
      });
    } else {
      const nextDay = new Date(lastDate);
      nextDay.setDate(nextDay.getDate() + 1);
      appendDay({
        date: nextDay,
        slots: [{ startTime: "09:00", endTime: "10:00" }],
      });
    }

    clearErrors("days");
  }

  async function onSubmit(data) {
    const formattedData = {
      days: data.days.map((day) => ({
        ...day,
        // Format the Date object into 'yyyy-MM-dd' for the backend
        date: day.date ? formatDate(day.date) : null,
      })),
    };

    try {
      await createScheduleSlots(formattedData, doctorId);
      toast.success("Slots created successfully");
      closeSheet?.();
      router.refresh();
    } catch (err) {
      console.error(err);
      if (err.status === 400) {
        toast.error(err?.message);
        return;
      }
      toast.error("Failed to create schedule slots.");
    }
  }

  return (
    <SheetForm onSubmit={handleSubmit(onSubmit)}>
      <SheetBody>
        {days.map((day, index) => (
          <ScheduleSlotsDayItem
            key={day.id}
            control={control}
            register={register}
            dayIndex={index}
            getValues={getValues}
            isSubmitting={isSubmitting}
            trigger={trigger}
            errors={errors}
            onDeleteDay={() => removeDay(index)}
            isRemovable={days.length > 1}
          />
        ))}

        <div>
          <button
            type="button"
            className="hover:border-primary bg-surface hover:bg-surface-overlay text-primary flex w-full cursor-pointer items-center justify-center gap-2 rounded-lg border-2 border-dashed px-1 py-3 transition-all"
            onClick={handleAddDay}
            disabled={isSubmitting}
          >
            <CirclePlus />
            Add Another Day
          </button>
          {errors?.days?.root?.message && (
            <ErrorMessage
              message={errors?.days?.root?.message}
              className="mt-3 justify-center py-2 text-sm"
              withBg
            />
          )}
        </div>
      </SheetBody>
      <SheetFooter className="border-border shrink-0 border-t pt-4">
        <Button
          variation="ghost"
          type="button"
          onClick={() => {
            if (isDirty) reset(getDefaultValue());
          }}
          disabled={isSubmitting}
        >
          Reset
        </Button>
        <div>
          <Button className="w-full" type="submit" disabled={isSubmitting}>
            {isSubmitting ? <SpinnerMini /> : "Save Changes"}
          </Button>
        </div>
      </SheetFooter>
    </SheetForm>
  );
}

export default ScheduleSlotsForm;
