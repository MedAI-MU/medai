"use client";

import { useState } from "react";
import toast from "react-hot-toast";
import {
  Eye,
  EyeOff,
  IdCard,
  Lock,
  Mail,
  Phone,
  UsersRound,
} from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { signupSchema } from "@/lib/zod/schemas";
import { signupAction } from "@/lib/actions/auth";
import { ROLES } from "@/constants/roles";
import FormInput from "@/components/ui/FormInput";
import Button from "@/components/ui/Button";
import FormSelect from "@/components/ui/FormSelect";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { useRouter } from "next/navigation";

function SignupForm() {
  const [showPassword, setShowPassword] = useState(false);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ resolver: zodResolver(signupSchema) });
  const router = useRouter();

  async function onSubmit(data) {
    const res = await signupAction(data);
    const { fieldErrors, success, message, user } = res || {};

    // 1) Handle fields error came from server | backend
    if (fieldErrors) {
      for (const errorKey in fieldErrors)
        setError(errorKey, { message: fieldErrors[errorKey] });

      return;
    }

    // 2) Any other error will be shown in root
    if (!success) {
      setError("root", { message });
      return;
    }

    toast.success(message);
    router.replace(`/${user?.role}/dashboard`);
  }

  return (
    <form
      className="gap-form mt-8 flex flex-col"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FormSelect
        label="Role"
        options={ROLES}
        startIcon={<UsersRound />}
        defaultValue={ROLES[0]}
        {...register("role")}
        error={errors?.role?.message}
      />
      <div className="gap-form grid md:grid-cols-2">
        <FormInput
          type="text"
          label="Full Name"
          autoComplete="name"
          placeholder="John Doe"
          startIcon={<IdCard />}
          {...register("name")}
          error={errors?.name?.message}
        />
        <FormInput
          type="tel"
          label="Phone"
          autoComplete="tel"
          placeholder="01X XXX XXXX"
          startIcon={<Phone />}
          {...register("phone")}
          error={errors?.phone?.message}
        />
      </div>
      <FormInput
        type="email"
        label="Email"
        autoComplete="email"
        placeholder="john@example.com"
        startIcon={<Mail />}
        {...register("email")}
        error={errors?.email?.message}
      />
      <FormInput
        type={showPassword ? "text" : "password"}
        label="Password"
        autoComplete="new-password"
        placeholder="••••••••"
        startIcon={<Lock />}
        endIcon={
          showPassword ? (
            <Eye onClick={() => setShowPassword(false)} />
          ) : (
            <EyeOff onClick={() => setShowPassword(true)} />
          )
        }
        {...register("password")}
        error={errors?.password?.message}
      />
      <div className="mt-6 text-center">
        <Button type="submit" className="w-full" disabled={isSubmitting}>
          Sign Up
        </Button>
        {errors?.root?.message && (
          <ErrorMessage
            message={errors?.root?.message}
            className="font-semibold"
          />
        )}
      </div>
    </form>
  );
}

export default SignupForm;
