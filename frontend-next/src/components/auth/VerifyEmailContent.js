"use client";

import { useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import toast from "react-hot-toast";
import { LoaderCircle, CheckCircle, XCircle } from "lucide-react";
import { verifyEmail } from "@/services/client/auth";
import Button from "@/components/ui/Button";

function VerifyEmailContent() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const token = searchParams.get("token");
  const [status, setStatus] = useState("loading"); // loading | success | error
  const [errorMsg, setErrorMsg] = useState("");

  useEffect(() => {
    if (!token) {
      setStatus("error");
      setErrorMsg("Invalid verification link. No token provided.");
      return;
    }

    verifyEmail(token).then((res) => {
      if (res.success) {
        setStatus("success");
        toast.success("Email verified successfully!");
      } else {
        setStatus("error");
        setErrorMsg(
          res.message || "Verification failed. The link may have expired.",
        );
      }
    });
  }, [token]);

  if (status === "loading") {
    return (
      <div className="mt-8 flex flex-col items-center text-center">
        <LoaderCircle className="size-10 animate-spin text-primary" />
        <p className="text-text-muted mt-4">Verifying your email...</p>
      </div>
    );
  }

  if (status === "success") {
    return (
      <div className="mt-8 flex flex-col items-center text-center">
        <div className="bg-emerald-500/10 mb-4 flex size-16 items-center justify-center rounded-full">
          <CheckCircle className="size-8 text-emerald-500" />
        </div>
        <h3 className="text-text-base text-lg font-semibold">
          Email Verified!
        </h3>
        <p className="text-text-muted mt-2 mb-6 max-w-sm">
          Your email has been successfully verified. You can now log in.
        </p>
        <Button onClick={() => router.replace("/auth/login")}>
          Go to Login
        </Button>
      </div>
    );
  }

  return (
    <div className="mt-8 flex flex-col items-center text-center">
      <div className="bg-danger/10 mb-4 flex size-16 items-center justify-center rounded-full">
        <XCircle className="size-8 text-danger" />
      </div>
      <h3 className="text-text-base text-lg font-semibold">
        Verification Failed
      </h3>
      <p className="text-text-muted mt-2 mb-6 max-w-sm">{errorMsg}</p>
      <Button onClick={() => router.replace("/auth/login")}>
        Back to Login
      </Button>
    </div>
  );
}

export default VerifyEmailContent;
