import Link from "next/link";

const Variations = {
  primary: "bg-primary text-white font-medium hover:opacity-90",
  secondary:
    "border-2 border-primary text-primary font-medium hover:bg-primary/10",
  ghost:
    "text-text-muted font-medium hover:bg-surface-overlay hover:text-text-base",
  danger: "bg-danger text-white font-medium hover:opacity-90",
  dangerGhost: "text-danger font-medium hover:bg-danger-muted",
};

function Button({
  variation = "primary",
  onClick,
  className = "",
  disabled,
  children,
  type,
  href,
  ...props
}) {
  const Style = `rounded-lg px-5 flex gap-4 items-center py-2 min-h-10 transition-all cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed ${Variations[variation]} ${className}`;

  if (href)
    return (
      <Link className={Style} href={href} {...props}>
        {children}
      </Link>
    );

  return (
    <button className={Style} onClick={onClick} disabled={disabled} type={type} {...props}>
      {children}
    </button>
  );
}

export default Button;
