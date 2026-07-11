"use client";

import { useRouter } from "next/navigation";
import { useForm } from "react-hook-form";
import toast from "react-hot-toast";
import { zodResolver } from "@hookform/resolvers/zod";
import { profileSchema } from "@/lib/zod/authSchemas";
import { formatDate, parseDate } from "@/lib/utils/DateTimeHelpers";
import { updateProfile } from "@/services/client/user";

import { SheetBody, SheetFooter } from "@/components/shadcn/sheet";
import SheetForm from "@/components/ui/SheetForm";
import FormInput from "@/components/ui/FormInput";
import FormSelect from "@/components/ui/FormSelect";
import FormDatePicker from "@/components/ui/FormDatePicker";
import Button from "@/components/ui/Button";
import SpinnerMini from "@/components/ui/SpinnerMini";
import TextArea from "@/components/ui/TextArea";

export default function ProfileEditForm({ user, closeSheet }) {
  const router = useRouter();

  const {
    register,
    handleSubmit,
    control,
    formState: { errors, isSubmitting, isDirty },
  } = useForm({
    resolver: zodResolver(profileSchema),
    defaultValues: {
      name: user?.name || "",
      phone: user?.phone || "",
      birthDate: user?.birthDate ? parseDate(user.birthDate) : undefined,
      gender: user?.gender || "",
      bio: user?.bio || "",
    },
  });

  async function onSubmit(data) {
    if (!isDirty) return;

    try {
      const payload = {
        ...data,
        birthDate: data?.birthDate ? formatDate(data.birthDate) : null,
        gender: data?.gender ? data.gender : null,
      };

      await updateProfile(user.id, payload);
      toast.success("Profile updated successfully.");
      closeSheet?.();
      router.refresh();
    } catch (err) {
      toast.error(err.message || "Failed to update profile.");
    }
  }

  return (
    <SheetForm onSubmit={handleSubmit(onSubmit)}>
      <SheetBody>
        <div className="space-y-6">
          <FormInput
            label="Full Name"
            placeholder="Enter your full name"
            {...register("name")}
            error={errors?.name?.message}
            disabled={isSubmitting}
          />

          <FormInput
            label="Phone"
            placeholder="010XXXXXXXX"
            {...register("phone")}
            error={errors?.phone?.message}
            disabled={isSubmitting}
          />

          <FormDatePicker
            name="birthDate"
            control={control}
            label="Birth Date"
            placeholder="Select birth date"
            disabled={isSubmitting}
            disabledRange={{ after: new Date() }}
          />

          <FormSelect
            label="Gender"
            defaultValue="Select gender"
            options={["male", "female"]}
            {...register("gender")}
            error={errors?.gender?.message}
            disabled={isSubmitting}
          />

          <TextArea
            label="Bio"
            rows={4}
            {...register("bio")}
            placeholder="Tell us about yourself..."
            error={errors?.bio?.message}
            disabled={isSubmitting}
          />
        </div>
      </SheetBody>

      <SheetFooter>
        <Button
          type="submit"
          variation="primary"
          disabled={isSubmitting || !isDirty}
        >
          {isSubmitting ? <SpinnerMini /> : "Save Changes"}
        </Button>
      </SheetFooter>
    </SheetForm>
  );
}
