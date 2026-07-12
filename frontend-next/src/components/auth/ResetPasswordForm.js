"use client";

import { useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import toast from "react-hot-toast";
import { Eye, EyeOff, Lock, CheckCircle } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { resetPasswordSchema } from "@/lib/zod/authSchemas";
import { resetPassword } from "@/services/client/auth";
import FormInput from "@/components/ui/FormInput";
import Button from "@/components/ui/Button";
import ErrorMessage from "@/components/ui/ErrorMessage";

function ResetPasswordForm() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const [showPassword, setShowPassword] = useState(false);
  const [done, setDone] = useState(false);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ resolver: zodResolver(resetPasswordSchema) });

  async function onSubmit(data) {
    if (!token) {
      setError("root", { message: "Invalid or missing reset token." });
      return;
    }

    const res = await resetPassword(token, data.password);

    if (!res.success) {
      setError("root", { message: res.message });
      return;
    }

    setDone(true);
    toast.success("Password reset successfully. Please log in.");
    router.replace("/auth/login");
  }

  if (!token) {
    return (
      <div className="mt-8 text-center">
        <p className="text-danger">
          Invalid reset link. Please request a new password reset.
        </p>
      </div>
    );
  }

  if (done) {
    return (
      <div className="mt-8 flex flex-col items-center text-center">
        <div className="mb-4 flex size-16 items-center justify-center rounded-full bg-emerald-500/10">
          <CheckCircle className="size-8 text-emerald-500" />
        </div>
        <h3 className="text-text-base text-lg font-semibold">
          Password Reset Successful
        </h3>
        <p className="text-text-muted mt-2">Redirecting to login...</p>
      </div>
    );
  }

  return (
    <form
      className="gap-form mt-8 flex flex-col"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FormInput
        type={showPassword ? "text" : "password"}
        label="New Password"
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
      <FormInput
        type="password"
        label="Confirm Password"
        autoComplete="new-password"
        placeholder="••••••••"
        startIcon={<Lock />}
        {...register("confirmPassword")}
        error={errors?.confirmPassword?.message}
      />
      <div className="mt-6 text-center">
        <Button type="submit" className="w-full" disabled={isSubmitting}>
          {isSubmitting ? "Resetting..." : "Reset Password"}
        </Button>
        {errors?.root?.message && (
          <ErrorMessage
            message={errors?.root?.message}
            className="justify-center font-semibold"
          />
        )}
      </div>
    </form>
  );
}

export default ResetPasswordForm;
