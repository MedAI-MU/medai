import Link from "next/link";

const Variations = {
  primary: "bg-primary text-white font-medium hover:opacity-90",
  secondary:
    "border-2 border-primary text-primary font-medium hover:bg-primary/10",
};

function Button({
  variation = "primary",
  onClick,
  className = "",
  disabled,
  children,
  type,
  href,
}) {
  const Style = `rounded-lg px-5 py-2 min-h-10 transition-all cursor-pointer disabled:opacity-70 disabled:cursor-not-allowed ${Variations[variation]} ${className}`;

  if (href)
    return (
      <Link className={Style} href={href}>
        {children}
      </Link>
    );

  return (
    <button className={Style} onClick={onClick} disabled={disabled} type={type}>
      {children}
    </button>
  );
}

export default Button;
