"use client";

import { useEffect } from "react";
import { useForm, useWatch } from "react-hook-form";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import { useAuth } from "@/contexts/AuthContext";
import { applyScheduleTemplate } from "@/services/client/schedule";
import { formatDate } from "@/lib/utils/DateTimeHelpers";

import { DialogFooter } from "@/components/shadcn/dialog";
import DialogBody from "@/components/ui/DialogBody";
import FormDatePicker from "@/components/ui/FormDatePicker";
import Button from "@/components/ui/Button";
import SpinnerMini from "@/components/ui/SpinnerMini";
import ErrorMessage from "@/components/ui/ErrorMessage";

function ApplyTemplateForm({ closeModal, templateId }) {
  const { user } = useAuth();
  const {
    handleSubmit,
    control,
    formState: { isSubmitting, isSubmitted, errors },
    trigger,
    setError,
  } = useForm();
  const startDate = useWatch({ control, name: "startDate" });
  const router = useRouter();

  useEffect(() => {
    if (isSubmitted) trigger("endDate");
  }, [startDate, trigger, isSubmitted]);

  async function onSubmit(data) {
    const formatedDates = {
      startDate: formatDate(data?.startDate),
      endDate: formatDate(data?.endDate),
    };

    try {
      await applyScheduleTemplate(formatedDates, user?.sub, templateId);

      closeModal?.();
      toast.success("Template applied successfully");
      router.refresh();
    } catch (err) {
      setError("root", {
        message:
          err?.status === 400
            ? err?.message
            : "Something went wrong, please try again.",
      });
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormDatePicker
          label="Start Date"
          name="startDate"
          placeholder="Pick start date"
          control={control}
          rules={{ required: "Please select a start date" }}
          disabledRange={{
            before: new Date(),
          }}
          disabled={isSubmitting}
        />
        <FormDatePicker
          label="End Date"
          name="endDate"
          placeholder="Pick end date."
          control={control}
          rules={{
            required: "Please select a end date.",
            validate: (value) => {
              if (!startDate || !value) return true;

              return (
                value >= startDate || "End date must be after the start date"
              );
            },
          }}
          disabledRange={{
            before: startDate || new Date(),
          }}
          disabled={isSubmitting}
        />

        {errors?.root?.message && (
          <ErrorMessage
            message={errors?.root?.message}
            className="bg-danger-muted rounded-sm px-2 py-1"
          />
        )}
      </DialogBody>

      <DialogFooter showCloseButton disableCloseButton={isSubmitting}>
        <Button disabled={isSubmitting}>
          {isSubmitting ? <SpinnerMini /> : "Apply"}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default ApplyTemplateForm;
