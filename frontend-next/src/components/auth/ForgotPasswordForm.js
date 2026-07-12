"use client";

import { useState } from "react";
import { Mail, CheckCircle } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { forgotPasswordSchema } from "@/lib/zod/authSchemas";
import { forgotPassword } from "@/services/client/auth";
import FormInput from "@/components/ui/FormInput";
import Button from "@/components/ui/Button";
import ErrorMessage from "@/components/ui/ErrorMessage";
import SpinnerMini from "../ui/SpinnerMini";

function ForgotPasswordForm() {
  const [sent, setSent] = useState(false);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ resolver: zodResolver(forgotPasswordSchema) });

  async function onSubmit(data) {
    const res = await forgotPassword(data.email);

    if (!res.success) {
      setError("root", { message: res.message });
      return;
    }

    setSent(true);
  }

  if (sent) {
    return (
      <div className="mt-8 flex flex-col items-center text-center">
        <div className="mb-4 flex size-16 items-center justify-center rounded-full bg-emerald-500/10">
          <CheckCircle className="size-8 text-emerald-500" />
        </div>
        <h3 className="text-text-base text-lg font-semibold">
          Check Your Email
        </h3>
        <p className="text-text-muted mt-2 max-w-sm">
          If an account with that email exists, you will receive a password
          reset link shortly. Please check your inbox (and spam folder).
        </p>
      </div>
    );
  }

  return (
    <form
      className="gap-form mt-8 flex flex-col"
      onSubmit={handleSubmit(onSubmit)}
    >
      <FormInput
        type="email"
        label="Email"
        autoComplete="email"
        placeholder="john@example.com"
        startIcon={<Mail />}
        {...register("email")}
        error={errors?.email?.message}
      />
      <div className="mt-6 text-center">
        <Button type="submit" className="w-full" disabled={isSubmitting}>
          {!isSubmitting && "Send Reset Link"}
          {isSubmitting && (
            <>
              <SpinnerMini /> Sending...
            </>
          )}
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

export default ForgotPasswordForm;
