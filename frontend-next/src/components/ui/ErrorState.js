"use client";

import { RefreshCcw, TriangleAlert } from "lucide-react";
import Button from "./Button";
import IconBadge from "./IconBadge";
import Heading from "./Heading";
import { useRouter } from "next/navigation";

function ErrorState({
  title = "Something went wrong",
  description,
  icon: Icon = TriangleAlert,
  onRetry,
}) {
  const router = useRouter();
  return (
    <div className="flex items-center justify-center py-10">
      <div className="bg-surface border-border w-full max-w-lg rounded-xl border p-12 text-center shadow-md">
        <IconBadge
          color="red"
          icon={<Icon size={30} />}
          isRounded
          className="mx-auto mb-8"
        />
        <Heading Tag="h3" title={title} size="lg" />
        <p className="text-text-muted mt-4 mb-10 leading-relaxed">
          {description}
        </p>
        <div className="flex flex-col items-center gap-4">
          <Button onClick={onRetry || (() => router.refresh())}>
            <RefreshCcw />
            Try Again
          </Button>
        </div>
      </div>
    </div>
  );
}

export default ErrorState;
