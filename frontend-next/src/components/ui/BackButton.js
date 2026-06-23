"use client";
import { ChevronLeft } from "lucide-react";
import { useRouter } from "next/navigation";

function BackButton({ title = "" }) {
  const router = useRouter();

  return (
    <button
      className="text-text-muted hover:text-primary group inline-flex cursor-pointer items-center gap-1 text-sm transition-colors"
      onClick={() => router.back()}
    >
      <ChevronLeft
        size={16}
        className="transition-transform group-hover:-translate-x-0.5"
      />
      {title}
    </button>
  );
}

export default BackButton;
