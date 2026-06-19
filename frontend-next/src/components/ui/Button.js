"use client";

import Link from "next/link";
import { cn } from "@/lib/utils";

const Variations = {
  primary: "bg-primary text-white hover:opacity-90",
  secondary: "border-2 border-primary text-primary hover:bg-primary/10",
  outline:
    "border-border text-text-muted hover:bg-surface-overlay focus:ring-primary border bg-surface transition-colors focus:ring-2 focus:ring-offset-1 focus:outline-none",
  ghost: "text-text-muted hover:bg-surface-overlay hover:text-text-base",
  danger: "bg-danger text-white hover:opacity-90",
  dangerGhost: "text-danger hover:bg-danger-muted",
  green: "text-white bg-emerald-500 hover:bg-emerald-600",
};

const Sizes = {
  sm: "px-3 py-1.5 text-sm min-h-8",
  md: "px-5 py-2 min-h-10",
  lg: "px-6 py-3 min-h-12 text-base",
};

function Button({
  variation = "primary",
  onClick,
  className = "",
  children,
  startIcon,
  endIcon,
  type,
  size = "md",
  href,
  ...props
}) {
  const Style = cn(
    "rounded-lg flex justify-center font-medium gap-2 shrink-0 items-center transition-all cursor-pointer data-[disabled=true]:opacity-70 data-[disabled=true]:cursor-not-allowed disabled:opacity-70 disabled:cursor-not-allowed disabled:pointer-events-none active:scale-95 transition-all",
    Variations[variation],
    Sizes[size],
    className,
  );

  if (href)
    return (
      <Link
        className={Style}
        href={href}
        onClick={(e) => {
          if (e.currentTarget.dataset.disabled === "true") e.preventDefault();
          onClick?.();
        }}
        {...props}
      >
        {startIcon && startIcon}
        {children}
        {endIcon && endIcon}
      </Link>
    );

  return (
    <button className={Style} onClick={onClick} type={type} {...props}>
      {startIcon && startIcon}
      {children}
      {endIcon && endIcon}
    </button>
  );
}

export default Button;
