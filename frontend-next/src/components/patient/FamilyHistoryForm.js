import SpinnerMini from "@/components/ui/SpinnerMini";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import TextArea from "@/components/ui/TextArea";
import FormSelect from "@/components/ui/FormSelect";
import { DialogFooter } from "@/components/shadcn/dialog";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import DialogBody from "@/components/ui/DialogBody";
import { PATIENT_RELATIONS } from "@/constants/patient";
import {
  addFamilyHistory,
  updateFamilyHistory,
} from "@/services/client/patient";

function FamilyHistoryForm({ patientId, closeModal, recordToEdit = {} }) {
  const router = useRouter();
  const { relation, condition, notes, id: recordId } = recordToEdit;
  const isEdit = Boolean(recordId);
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: isEdit ? { relation, condition, notes } : {},
  });

  async function onSubmit(data) {
    try {
      if (isEdit) {
        if (!isDirty) return;
        await updateFamilyHistory({ ...data, recordId }, patientId);
      } else {
        await addFamilyHistory(data, patientId);
      }

      toast.success(
        `Family history record ${isEdit ? "edited" : "added"} successfully`,
      );
      closeModal();
      router.refresh();
    } catch (err) {
      console.error(err?.message);
      toast.error(`Failed to ${isEdit ? "edit" : "add"} family history record`);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormSelect
          label="Relation to Patient"
          defaultValue="Select relation..."
          options={PATIENT_RELATIONS}
          {...register("relation", { required: "Relation is required" })}
          disabled={isSubmitting}
          error={errors?.relation?.message}
        />
        <FormInput
          label="Condition"
          placeholder="e.g. Hypertension, Type 2 Diabetes"
          {...register("condition", { required: "Condition is required" })}
          disabled={isSubmitting}
          error={errors?.condition?.message}
        />
        <TextArea
          rows={3}
          label="Notes (Optional)"
          placeholder="Include any relevant details like age of diagnosis..."
          {...register("notes")}
          disabled={isSubmitting}
          error={errors?.notes?.message}
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
              "Add Record"
            )
          ) : (
            <SpinnerMini />
          )}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default FamilyHistoryForm;
