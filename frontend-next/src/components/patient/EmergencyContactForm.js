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
  addEmergencyContact,
  updateEmergencyContact,
} from "@/services/client/patient";

function EmergencyContactForm({ patientId, closeModal, contactToEdit = {} }) {
  const router = useRouter();
  const {
    id: contactId,
    name,
    relation,
    phoneNumber,
    email,
    address,
    notes,
  } = contactToEdit;
  const isEdit = Boolean(contactId);

  const {
    handleSubmit,
    register,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: isEdit
      ? { name, relation, phoneNumber, email, address, notes }
      : {},
  });

  async function onSubmit(data) {
    try {
      if (isEdit) {
        if (!isDirty) return;
        await updateEmergencyContact({ ...data, contactId }, patientId);
      } else {
        await addEmergencyContact(data, patientId);
      }

      toast.success(
        `Emergency contact ${isEdit ? "updated" : "added"} successfully`,
      );
      closeModal();
      router.refresh();
    } catch (err) {
      console.error(err?.message);
      toast.error(
        `Failed to ${isEdit ? "update" : "add"} emergency contact`,
      );
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <DialogBody>
        <FormInput
          label="Name"
          placeholder="Contact name"
          {...register("name", { required: "Name is required" })}
          disabled={isSubmitting}
          error={errors?.name?.message}
        />
        <FormSelect
          label="Relation to Patient"
          defaultValue="Select relation..."
          options={PATIENT_RELATIONS}
          {...register("relation", { required: "Relation is required" })}
          disabled={isSubmitting}
          error={errors?.relation?.message}
        />
        <FormInput
          label="Phone Number"
          placeholder="e.g. 01012345678"
          {...register("phoneNumber", { required: "Phone number is required" })}
          disabled={isSubmitting}
          error={errors?.phoneNumber?.message}
        />
        <FormInput
          label="Email (Optional)"
          type="email"
          placeholder="contact@example.com"
          {...register("email")}
          disabled={isSubmitting}
          error={errors?.email?.message}
        />
        <FormInput
          label="Address (Optional)"
          placeholder="123 Main St"
          {...register("address")}
          disabled={isSubmitting}
          error={errors?.address?.message}
        />
        <TextArea
          rows={3}
          label="Notes (Optional)"
          placeholder="Include any relevant details like best time to call..."
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
              "Add Contact"
            )
          ) : (
            <SpinnerMini />
          )}
        </Button>
      </DialogFooter>
    </form>
  );
}

export default EmergencyContactForm;
