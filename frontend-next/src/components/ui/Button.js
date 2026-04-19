import Link from "next/link";
import { forwardRef } from "react";
import { cn } from "@/lib/utils";

const Variations = {
  primary: "bg-primary text-white hover:opacity-90",
  secondary: "border-2 border-primary text-primary hover:bg-primary/10",
  ghost: "text-text-muted hover:bg-surface-overlay hover:text-text-base",
  danger: "bg-danger text-white hover:opacity-90",
  dangerGhost: "text-danger hover:bg-danger-muted",
  green: "text-white bg-emerald-500 hover:bg-emerald-600",
};

const Button = forwardRef(
  (
    {
      variation = "primary",
      onClick,
      className = "",
      disabled,
      children,
      type,
      href,
      ...props
    },
    ref,
  ) => {
    const Style = cn(
      "rounded-lg px-5 flex justify-center font-medium gap-4 items-center py-2 min-h-10 transition-all cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed",
      Variations[variation],
      className,
    );

    if (href)
      return (
        <Link className={Style} href={href} ref={ref} {...props}>
          {children}
        </Link>
      );

    return (
      <button
        className={Style}
        onClick={onClick}
        disabled={disabled}
        type={type}
        ref={ref}
        {...props}
      >
        {children}
      </button>
    );
  },
);

Button.displayName = "Button";

export default Button;
