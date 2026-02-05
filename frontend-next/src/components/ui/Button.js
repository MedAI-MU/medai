const Variations = {
  primary: "text-white hover:opacity-90 bg-primary-blue",
  secondary:
    "border-primary-blue border-2 text-primary-blue border hover:bg-blue-50",
};

function Button({ variation = "primary", className, disabled, children }) {
  return (
    <button
      disabled={disabled}
      className={`rounded-lg px-5 py-2 transition-all ${Variations[variation]} ${className}`}
    >
      {children}
    </button>
  );
}

export default Button;
