"use client";

import { useState } from "react";
import toast from "react-hot-toast";
import { Eye, EyeOff, Lock, Mail } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema } from "@/lib/zod/schemas";
import { loginAction } from "@/lib/actions/auth";
import FormInput from "@/components/ui/FormInput";
import Button from "@/components/ui/Button";
import ErrorMessage from "@/components/ui/ErrorMessage";
import { useRouter } from "next/navigation";

function LoginForm() {
  const [showPassword, setShowPassword] = useState(false);
  const {
    register,
    handleSubmit,
    setError,
    formState: { errors, isSubmitting },
  } = useForm({ resolver: zodResolver(loginSchema) });
  const router = useRouter();

  async function onSubmit(data) {
    const res = await loginAction(data);
    const { fieldErrors, success, message, user } = res || {};

    // 1) Handle fields error came from server
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
          Log In
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

export default LoginForm;
