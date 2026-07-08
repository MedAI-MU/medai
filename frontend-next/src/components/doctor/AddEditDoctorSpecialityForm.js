"use client";

import { useForm } from "react-hook-form";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import Button from "@/components/ui/Button";
import FormInput from "@/components/ui/FormInput";
import FormSelect from "@/components/ui/FormSelect";
import Checkbox from "@/components/ui/Checkbox";
import Badge from "@/components/ui/Badge";
import SpinnerMini from "@/components/ui/SpinnerMini";
import { DialogFooter } from "../shadcn/dialog";
import {
  addDoctorSpeciality,
  updateDoctorSpeciality,
} from "@/services/client/doctors";

export default function AddEditDoctorSpecialityForm({
  closeModal,
  doctorId,
  speciality,
  allSpecialities = [],
}) {
  const isEdit = Boolean(speciality?.id);
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm({
    defaultValues: isEdit
      ? {
          isPrimary: speciality.isPrimary,
          yearsOfExperience: speciality.yearsOfExperience,
        }
      : {},
  });

  async function onSubmit(data) {
    try {
      if (isEdit) {
        await updateDoctorSpeciality(doctorId, speciality.id, {
          isPrimary: data.isPrimary,
          yearsOfExperience: Number(data.yearsOfExperience) || 0,
        });
        toast.success("Speciality updated successfully");
      } else {
        const selected = allSpecialities.find(
          (s) => s.name === data.specialityName,
        );
        await addDoctorSpeciality(doctorId, {
          specialityId: selected.id,
          isPrimary: data.isPrimary || false,
          yearsOfExperience: Number(data.yearsOfExperience) || 0,
        });
        toast.success("Speciality added successfully");
      }
      closeModal?.();
      router.refresh();
    } catch {
      toast.error(`Failed to ${isEdit ? "update" : "add"} speciality`);
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
      {isEdit ? (
        <div className="space-y-2">
          <p className="text-text-muted text-xs font-bold tracking-wider uppercase">
            Speciality
          </p>
          <Badge color="blue" text={speciality.speciality?.name} />
        </div>
      ) : (
        <FormSelect
          label="Speciality"
          defaultValue="Select a speciality..."
          options={allSpecialities.map((s) => s.name)}
          {...register("specialityName", {
            required: "Please select a speciality",
          })}
          error={errors?.specialityName?.message}
        />
      )}

      <Checkbox label="Set as primary speciality" {...register("isPrimary")} />

      <FormInput
        type="text"
        label="Years of Experience"
        placeholder="e.g. 5"
        inputMode="numeric"
        {...register("yearsOfExperience", {
          required: "Years of experience is required",
          validate: (value) => Number(value) >= 0 || "Must be 0 or more",
          onChange: (e) => (e.target.value = e.target.value.replace(/\D/g, "")),
        })}
        error={errors?.yearsOfExperience?.message}
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
