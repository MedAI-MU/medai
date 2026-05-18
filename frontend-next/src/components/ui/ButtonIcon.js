"use client";

import { cn } from "@/lib/utils";

function ButtonIcon({ children, onClick, className = "", ...props }) {
  return (
    <button
      className={cn(
        "text-text-muted hover:bg-surface-overlay rounded-lg p-2 transition-colors active:scale-95",
        className,
      )}
      onClick={onClick}
      {...props}
    >
      {children}
    </button>
  );
}

export default ButtonIcon;
