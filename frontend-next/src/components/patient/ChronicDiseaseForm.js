import { DialogFooter } from "@/components/shadcn/dialog";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import {
  addChronicDisease,
  updateChronicDiesease,
} from "@/services/client/patient";
import { format } from "date-fns";
import toast from "react-hot-toast";
import SpinnerMini from "@/components/ui/SpinnerMini";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import TextArea from "@/components/ui/TextArea";
import FormDatePicker from "@/components/ui/FormDatePicker";
import DialogBody from "@/components/ui/DialogBody";

function AddChronicForm({ patientId, closeModal, ChronicToEdit = {} }) {
  const router = useRouter();
  const { name, description, diagnosisDate, id: chronicId } = ChronicToEdit;
  const isEdit = Boolean(chronicId);
  const {
    handleSubmit,
    register,
    reset,
    control,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: isEdit ? { name, description, diagnosisDate } : {},
  });

  async function onSubmit(data) {
    const dataAfterDateFormat = {
      ...data,
      diagnosisDate: format(data.diagnosisDate, "yyyy-MM-dd"),
    };

    try {
      if (isEdit) {
        if (!isDirty) return;
        await updateChronicDiesease(
          { ...dataAfterDateFormat, chronicId },
          patientId,
        );
      } else {
        await addChronicDisease(dataAfterDateFormat, patientId);
      }

      toast.success(
        `Chronic disease ${isEdit ? "edited" : "added"} successfully`,
      );
      closeModal();
      router.refresh();
    } catch (err) {
      console.error(err?.message);
      toast.error(`Failed to ${isEdit ? "edit" : "add"} chronic disease`);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormInput
          label="Chronic disease Name"
          placeholder="e.g. Diabetes, Hypertension, Asthma"
          {...register("name", { required: "Name required" })}
          disabled={isSubmitting}
          error={errors?.name?.message}
        />
        <FormDatePicker
          name="diagnosisDate"
          label="Diagnosis Date"
          control={control}
          rules={{ required: "Diagnosis date is required" }}
          disabled={isSubmitting}
          disabledRange={(date) => date > new Date()}
        />
        <TextArea
          rows={3}
          label="Description"
          placeholder="Describe the condition, symptoms, or current treatment..."
          {...register("description", {
            required: "Description required",
          })}
          disabled={isSubmitting}
          error={errors?.description?.message}
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
              "Add Chronic Disease"
            )
          ) : (
            <SpinnerMini />
          )}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default AddChronicForm;
