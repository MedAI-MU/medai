"use client";

import { useState } from "react";
import toast from "react-hot-toast";
import { Eye, EyeOff, Lock, Mail } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { loginSchema } from "@/lib/zod/authSchemas";
import { loginAction } from "@/services/client/auth";
import FormInput from "@/components/ui/FormInput";
import Button from "@/components/ui/Button";
import ErrorMessage from "@/components/ui/ErrorMessage";
import Link from "next/link";
import { useRouter } from "next/navigation";
import SpinnerMini from "../ui/SpinnerMini";

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

    if (user?.status === "pending" && user?.role !== "patient") {
      toast.success("Logged in. Your account is pending approval.");
      router.replace("/auth/pending-approval");
    } else {
      toast.success(message);
      router.replace(`/${user?.role}`);
    }
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
        disabled={isSubmitting}
      />
      <FormInput
        type={showPassword ? "text" : "password"}
        label="Password"
        autoComplete="current-password"
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
        disabled={isSubmitting}
      />
      <div className="mt-1 text-right">
        <Link
          href="/auth/forgot-password"
          className="text-primary text-sm font-medium underline-offset-2 hover:underline"
          onClick={(e) => {
            if (isSubmitting) e.preventDefault();
          }}
        >
          Forgot Password?
        </Link>
      </div>
      <div className="mt-6 text-center">
        <Button type="submit" className="w-full" disabled={isSubmitting}>
          {isSubmitting ? <SpinnerMini /> : "Log In"}
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

export default LoginForm;
