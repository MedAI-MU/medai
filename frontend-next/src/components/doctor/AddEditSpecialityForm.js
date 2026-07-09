"use client";

import { useForm } from "react-hook-form";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import SpinnerMini from "@/components/ui/SpinnerMini";
import { createSpeciality, updateSpeciality } from "@/services/client/doctors";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import { DialogFooter } from "../shadcn/dialog";

export default function AddEditSpecialityForm({ closeModal, speciality = {} }) {
  const isEdit = Boolean(speciality?.id);
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({ defaultValues: isEdit ? { name: speciality?.name } : {} });

  async function onSubmit(data) {
    try {
      if (isEdit)
        await updateSpeciality(speciality?.id, { name: data?.name?.trim() });
      else await createSpeciality({ name: data?.name?.trim() });

      toast.success(`Speciality ${isEdit ? "Editted" : "added"} successfully`);
      closeModal?.();
      router.refresh();
    } catch {
      toast.error(`Failed to ${isEdit ? "edit" : "add"} speciality`);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      <FormInput
        label="Speciality Name"
        placeholder="e.g. Cardiology, Neurology..."
        {...register("name", { required: "Speciality name is required" })}
        error={errors?.name?.message}
      />
      <DialogFooter>
        <Button type="button" variation="ghost" onClick={closeModal}>
          Cancel
        </Button>
        <Button type="submit" disabled={isSubmitting}>
          {isSubmitting ? <SpinnerMini /> : "Save"}
        </Button>
      </DialogFooter>
    </form>
  );
}
