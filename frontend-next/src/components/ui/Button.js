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
}) {
  return (
    <button
      className={`rounded-lg px-5 py-2 transition-all ${Variations[variation]} ${className}`}
      onClick={onClick}
      disabled={disabled}
      type={type}
    >
      {children}
    </button>
  );
}

export default Button;
