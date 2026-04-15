import SpinnerMini from "@/components/ui/SpinnerMini";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import TextArea from "@/components/ui/TextArea";
import { DialogFooter } from "@/components/shadcn/dialog";
import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { addAllergy, updateAllergy } from "@/services/client/patient";
import DialogBody from "../ui/DialogBody";

function AllergyForm({ patientId, closeModal, allergyToEdit = {} }) {
  const router = useRouter();
  const { name, description, id: allergyId } = allergyToEdit;
  const isEdit = Boolean(allergyId);
  const {
    handleSubmit,
    register,
    reset,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: isEdit ? { name, description } : {} });

  async function onSubmit(data) {
    try {
      if (isEdit) {
        await updateAllergy({ ...data, allergyId }, patientId);
      } else {
        await addAllergy(data, patientId);
      }

      toast.success(`Allergy ${isEdit ? "edited" : "added"} successfully`);
      closeModal();
      router.refresh();
    } catch (err) {
      console.error(err?.message);
      toast.error(`Failed to ${isEdit ? "edit" : "add"} allergy`);
    }
  }
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormInput
          label="Allergy Name"
          placeholder="e.g. Penuts, Penicillin"
          {...register("name", { required: "Name required" })}
          disabled={isSubmitting}
          error={errors?.name?.message}
        />
        <TextArea
          rows={3}
          label="Description"
          placeholder="Describe the reaction and severity..."
          {...register("description", {
            required: "Description required",
          })}
          disabled={isSubmitting}
          error={errors?.description?.message}
        />
      </DialogBody>
      <DialogFooter showCloseButton={!isEdit}>
        {isEdit && (
          <Button
            type="button"
            variation="ghost"
            onClick={() => reset()}
            disabled={isSubmitting}
          >
            Reset
          </Button>
        )}
        <Button disabled={isSubmitting}>
          {!isSubmitting ? "Add Allergy" : <SpinnerMini />}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default AllergyForm;
