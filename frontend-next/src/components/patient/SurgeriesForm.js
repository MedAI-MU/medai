import { DialogFooter } from "@/components/shadcn/dialog";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import { addSurgery, updateSurgery } from "@/services/client/patient";
import { format } from "date-fns";
import toast from "react-hot-toast";
import SpinnerMini from "@/components/ui/SpinnerMini";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import TextArea from "@/components/ui/TextArea";
import FormDatePicker from "@/components/ui/FormDatePicker";
import DialogBody from "@/components/ui/DialogBody";

function SurgeriesForm({ patientId, closeModal, surgeryToEdit = {} }) {
  const router = useRouter();
  const { name, description, date, id: surgeryId } = surgeryToEdit;
  const isEdit = Boolean(surgeryId);
  const {
    handleSubmit,
    register,
    reset,
    control,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: isEdit ? { name, description, date } : {},
  });

  async function onSubmit(formData) {
    const dataAfterDateFormat = {
      ...formData,
      date: format(formData.date, "yyyy-MM-dd"),
    };

    try {
      if (isEdit) {
        if (!isDirty) return;
        await updateSurgery({ ...dataAfterDateFormat, surgeryId }, patientId);
      } else {
        await addSurgery(dataAfterDateFormat, patientId);
      }

      toast.success(`Surgery ${isEdit ? "edited" : "added"} successfully`);
      closeModal();
      router.refresh();
    } catch (err) {
      console.error(err?.message);
      toast.error(`Failed to ${isEdit ? "edit" : "add"} surgery`);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormInput
          label="Surgery Name"
          placeholder="e.g. Diabetes, Hypertension, Asthma"
          {...register("name", { required: "Name required" })}
          disabled={isSubmitting}
          error={errors?.name?.message}
        />
        <FormDatePicker
          name="date"
          label="Surgery Date"
          control={control}
          rules={{ required: "Surgery date is required" }}
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
              "Add Surgery"
            )
          ) : (
            <SpinnerMini />
          )}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default SurgeriesForm;
