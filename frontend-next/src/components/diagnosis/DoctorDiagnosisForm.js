"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import SpinnerMini from "@/components/ui/SpinnerMini";
import Button from "@/components/ui/Button";
import TextArea from "@/components/ui/TextArea";
import DialogBody from "@/components/ui/DialogBody";
import { DialogFooter } from "@/components/shadcn/dialog";
import { createDiagnosis, updateDiagnosis } from "@/services/client/diagnosis";

function DoctorDiagnosisForm({
  patientUserId,
  appointmentId,
  diagnosisToEdit,
  closeModal,
}) {
  const router = useRouter();
  const { symptoms, summary, id: diagnosisId } = diagnosisToEdit || {};
  const isEdit = Boolean(diagnosisId);
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: isEdit
      ? { symptoms, summary }
      : { symptoms: "", summary: "" },
  });

  async function onSubmit(data) {
    if (isEdit && !isDirty) return;

    try {
      const payload = {
        symptoms: data.symptoms.trim(),
        summary: data.summary.trim(),
      };

      if (isEdit) {
        await updateDiagnosis(patientUserId, diagnosisId, payload);
      } else {
        await createDiagnosis(patientUserId, {
          ...payload,
          appointmentId,
        });
      }

      toast.success(`Diagnosis ${isEdit ? "updated" : "added"} successfully`);
      closeModal?.();
      router.refresh();
    } catch (err) {
      toast.error(
        err?.message || `Failed to ${isEdit ? "update" : "add"} diagnosis`,
      );
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <TextArea
          rows={3}
          label="Symptoms"
          placeholder="Describe the patient's symptoms..."
          {...register("symptoms", { required: "Symptoms are required" })}
          disabled={isSubmitting}
          error={errors?.symptoms?.message}
        />
        <TextArea
          rows={4}
          label="Diagnosis Summary"
          placeholder="Provide your diagnosis summary..."
          {...register("summary", { required: "Summary is required" })}
          disabled={isSubmitting}
          error={errors?.summary?.message}
        />
      </DialogBody>
      <DialogFooter showCloseButton={!isEdit} disableCloseButton={isSubmitting}>
        {isEdit && (
          <Button
            type="button"
            variation="ghost"
            onClick={() => reset()}
            disabled={isSubmitting || !isDirty}
          >
            Reset
          </Button>
        )}
        <Button disabled={isSubmitting || (!isDirty && isEdit)}>
          {!isSubmitting ? (
            isEdit ? (
              "Save Changes"
            ) : (
              "Add Diagnosis"
            )
          ) : (
            <SpinnerMini />
          )}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default DoctorDiagnosisForm;
