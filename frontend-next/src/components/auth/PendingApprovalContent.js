"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Hourglass, LogOut, RefreshCw } from "lucide-react";
import { getUser } from "@/services/client/auth";
import { logoutAction } from "@/lib/actions";
import DarkmodeToggler from "@/components/ui/DarkmodeToggler";
import Button from "@/components/ui/Button";
import SpinnerMini from "../ui/SpinnerMini";
import LogoutButton from "../ui/LogoutButton";

function PendingApprovalContent({ user }) {
  const router = useRouter();
  const [message, setMessage] = useState("");
  const [checking, setChecking] = useState(false);

  async function checkStatus() {
    setChecking(true);
    setMessage("");

    try {
      const data = await getUser();
      router.replace(`/${data.role}`);
    } catch (err) {
      if (err?.status === 403) {
        setMessage(
          "Your account is still pending approval. Please check again later.",
        );
      } else {
        setMessage("Unable to connect. Please try again.");
      }
    } finally {
      setChecking(false);
    }
  }

  return (
    <div className="relative flex flex-col items-center text-center">
      <DarkmodeToggler className="bg-surface-overlay border-border hover:text-primary hover:border-primary/50 shadow-glow absolute top-0 right-0 border transition-all duration-300" />

      <div className="mt-12 mb-6 flex size-20 items-center justify-center rounded-full bg-amber-500/10">
        <Hourglass className="size-10 text-amber-500" />
      </div>

      <h2 className="text-text-base mb-2 text-3xl font-bold">
        Approval Pending
      </h2>
      <p className="text-text-muted mb-8 max-w-md">
        Your account is awaiting verification by an administrator. You will be
        notified once your account has been approved. Please check back later.
      </p>

      {user && (
        <div className="bg-surface-overlay border-border mb-6 w-full max-w-sm rounded-lg border p-4 text-left text-sm">
          <p className="text-text-muted">
            Email:{" "}
            <span className="text-text-base font-medium">{user.email}</span>
          </p>
          <p className="text-text-muted">
            Role:{" "}
            <span className="text-text-base font-medium capitalize">
              {user.role}
            </span>
          </p>
        </div>
      )}

      <div className="flex w-full max-w-xs flex-col gap-3">
        <Button
          type="button"
          onClick={checkStatus}
          disabled={checking}
          className="w-full"
        >
          <RefreshCw size={16} className={checking ? "animate-spin" : ""} />
          {checking ? "Checking..." : "Check Status"}
        </Button>

        <LogoutButton className="justify-center" />
      </div>

      {message && <p className="text-text-muted mt-6 text-sm">{message}</p>}
    </div>
  );
}

export default PendingApprovalContent;
