"use client";

import { useForm } from "react-hook-form";
import FormInput from "@/components/ui/FormInput";
import FormSelect from "@/components/ui/FormSelect";
import Button from "@/components/ui/Button";
import { updatePatientPersonalInfo } from "@/services/client/patient";
import toast from "react-hot-toast";
import SpinnerMini from "@/components/ui/SpinnerMini";
import Heading from "@/components/ui/Heading";
import Grid from "@/components/ui/Grid";
import { BLOOD_TYPES, MARITAL_STATUSES } from "@/constants/patient";
import { useRouter } from "next/navigation";

function PersonalInfoTab({ data }) {
  const {
    register,
    handleSubmit,
    reset,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    defaultValues: {
      height: data?.height || "",
      weight: data?.weight || "",
      bloodType: data?.bloodType || "",
      maritalStatus: data?.maritalStatus || "",
    },
  });
  const router = useRouter();

  async function onSubmit(formData) {
    if (!isDirty) return;

    try {
      const response = await updatePatientPersonalInfo(formData, data?.userId);
      const { userId, updatedAt, createdAt, ...newData } = response;
      reset(newData);
      router.refresh();
      toast.success("Personal Info updated successfully");
    } catch (err) {
      toast.error("Something went wrong");
      console.error(err?.message);
    }
  }

  return (
    <div className="bg-surface border-border rounded-xl border p-8 shadow-sm">
      <Heading size="lg" title="Personal Health Information" className="mb-6" />

      <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
        <Grid cols="two" gap="gap-x-12 gap-y-6">
          {/* Readonly Fields */}
          <FormInput
            label="Full Name"
            defaultValue={data?.user?.name || ""}
            disabled={true}
          />
          <FormInput
            label="Gender"
            defaultValue={data?.user?.gender || "Not specified"}
            disabled={true}
          />

          {/* Editable Fields */}
          <FormInput
            label="Height (cm)"
            inputMode="numeric"
            {...register("height", {
              required: "Required",
              pattern: {
                value: /^[0-9]+$/,
                message: "Numbers only",
              },
              setValueAs: (val) => (val === "" ? "" : Number(val)),
            })}
            // Allow numbers only
            onInput={(e) => {
              e.target.value = e.target.value.replace(/\D/g, "");
            }}
            disabled={isSubmitting}
            error={errors?.height?.message}
          />
          <FormInput
            label="Weight (kg)"
            inputMode="numeric"
            {...register("weight", {
              required: "Required",
              pattern: {
                value: /^[0-9]+$/,
                message: "Numbers only",
              },
              setValueAs: (val) => (val === "" ? "" : Number(val)),
            })}
            onInput={(e) => {
              e.target.value = e.target.value.replace(/\D/g, "");
            }}
            disabled={isSubmitting}
            error={errors?.weight?.message}
          />
          <FormSelect
            label="Blood Type"
            defaultValue="Select blood type"
            options={BLOOD_TYPES}
            {...register("bloodType", {
              validate: (val) => BLOOD_TYPES.includes(val) || "Invalid choice",
            })}
            disabled={isSubmitting}
            error={errors?.bloodType?.message}
          />
          <FormSelect
            label="Marital Status"
            defaultValue="select marital status"
            options={["single", "married", "divorced", "widowed"]}
            {...register("maritalStatus", {
              validate: (val) =>
                MARITAL_STATUSES.includes(val) || "Invalid Choice",
            })}
            disabled={isSubmitting}
            error={errors?.maritalStatus?.message}
          />
        </Grid>

        <div className="border-border mt-10 flex justify-end gap-6 border-t pt-6">
          <Button
            onClick={() => reset()}
            type="button"
            variation="ghost"
            disabled={isSubmitting || !isDirty}
          >
            Reset
          </Button>
          <Button
            type="submit"
            variation="primary"
            disabled={isSubmitting || !isDirty}
          >
            {!isSubmitting ? "Save Changes" : <SpinnerMini />}
          </Button>
        </div>
      </form>
    </div>
  );
}

export default PersonalInfoTab;
