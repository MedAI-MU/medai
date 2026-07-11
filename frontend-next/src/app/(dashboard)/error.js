"use client";

import ErrorState from "@/components/ui/ErrorState";
import { useRouter } from "next/navigation";
import { startTransition } from "react";

function Error({ reset }) {
  const router = useRouter();

  function handleRetry() {
    startTransition(() => {
      router.refresh();
      reset();
    });
  }

  return (
    <ErrorState
      description="An unexpected error occurred in your dashboard. Please try again."
      onRetry={handleRetry}
    />
  );
}

export default Error;
