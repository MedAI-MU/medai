import Link from "next/link";

const Variations = {
  primary: "text-white hover:opacity-90 bg-primary-blue",
  secondary:
    "border-primary-blue border-2 text-primary-blue border hover:bg-blue-50",
};

function Button({
  variation = "primary",
  onClick,
  className,
  disabled,
  children,
  type,
  href,
}) {
  const Style = `rounded-lg px-5 py-2 transition-all cursor-pointer ${Variations[variation]} ${className}`;

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
