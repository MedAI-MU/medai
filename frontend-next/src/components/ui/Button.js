import Link from "next/link";
import { forwardRef } from "react";
import { cn } from "@/lib/utils";

const Variations = {
  primary: "bg-primary text-white font-medium hover:opacity-90",
  secondary:
    "border-2 border-primary text-primary font-medium hover:bg-primary/10",
  ghost:
    "text-text-muted font-medium hover:bg-surface-overlay hover:text-text-base",
  danger: "bg-danger text-white font-medium hover:opacity-90",
  dangerGhost: "text-danger font-medium hover:bg-danger-muted",
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
      "rounded-lg px-5 flex justify-center gap-4 items-center py-2 min-h-10 transition-all cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed",
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
