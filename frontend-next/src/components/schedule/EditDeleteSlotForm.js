"use client";

import { startOfDay } from "date-fns";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { useRouter } from "next/navigation";

import {
  deleteScheduleSlot,
  updateScheduleSlot,
} from "@/services/client/schedule";
import { useDoctorInfo } from "@/contexts/DoctorInfoContext";
import { formatDate, stripSeconds } from "@/lib/utils/DateTimeHelpers";
import { timeValidationWithDayDate } from "@/lib/zod/scheduleSchemas";
import { zodResolver } from "@hookform/resolvers/zod";

import { DialogFooter } from "@/components/shadcn/dialog";
import Button from "@/components/ui/Button";
import DialogBody from "@/components/ui/DialogBody";
import FormDatePicker from "@/components/ui/FormDatePicker";
import SpinnerMini from "@/components/ui/SpinnerMini";
import FormInput from "@/components/ui/FormInput";
import ErrorMessage from "@/components/ui/ErrorMessage";
import DeleteDialog from "@/components/ui/DeleteDialog";

function EditDeleteSlotForm({ slotToEdit = {}, closeModal }) {
  const defaultValues = {
    dayDate: slotToEdit?.day,
    startTime: stripSeconds(slotToEdit?.startTime),
    endTime: stripSeconds(slotToEdit?.endTime),
  };

  const {
    doctor: { doctorId },
  } = useDoctorInfo();
  const {
    register,
    handleSubmit,
    control,
    reset,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({
    resolver: zodResolver(timeValidationWithDayDate),
    defaultValues: defaultValues,
  });
  const router = useRouter();

  async function onSubmit(data) {
    const dataFormated = {
      dayDate: formatDate(data?.dayDate),
      startTime: stripSeconds(data?.startTime),
      endTime: stripSeconds(data?.endTime),
    };

    try {
      await updateScheduleSlot(dataFormated, doctorId, slotToEdit?.id);
      closeModal?.();
      toast.success("Slot date updated successfully");
      router.refresh();
    } catch (err) {
      if (err.message === "Invalid or overlapping time ranges detected") {
        setError("root", {
          message: "This slot overlaps with another existing slot",
        });

        return;
      }
      toast.error(err.message);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormDatePicker
          name="dayDate"
          control={control}
          required
          label="Date"
          placeholder="Pick a new date"
          disabledRange={{ before: startOfDay(new Date()) }}
          disabled={isSubmitting}
        />
        <FormInput
          type="time"
          label="Start Time"
          {...register("startTime")}
          error={errors?.startTime?.message}
        />
        <FormInput
          type="time"
          label="End Time"
          {...register("endTime")}
          error={errors?.endTime?.message}
        />
      </DialogBody>

      {errors?.root?.message && (
        <ErrorMessage message={errors?.root?.message} className="mb-4" withBg />
      )}

      <DialogFooter className="justify-between gap-2">
        <DeleteDialog
          title="Delete Slot?"
          description="Are you sure you want to delete this slot?"
          successMessage="Slot deleted successfully"
          failMessage="Failed to delete slot"
          onConfirm={async () => {
            await deleteScheduleSlot(doctorId, slotToEdit?.id);
            closeModal?.();
          }}
        >
          <Button variation="dangerGhost" type="button" disabled={isSubmitting}>
            Delete
          </Button>
        </DeleteDialog>
        <div className="flex gap-2">
          <Button
            variation="ghost"
            type="button"
            onClick={() => reset(defaultValues)}
            disabled={isSubmitting}
          >
            Reset
          </Button>
          <Button disabled={isSubmitting}>
            {isSubmitting ? <SpinnerMini /> : "Save Changes"}
          </Button>
        </div>
      </DialogFooter>
    </form>
  );
}

export default EditDeleteSlotForm;
