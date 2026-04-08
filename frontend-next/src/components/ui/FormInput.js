import ErrorMessage from "./ErrorMessage";

function FormInput({ label, error, startIcon, endIcon, ...attrs }) {
  let px = "";
  if (startIcon) px += "ps-10";
  if (endIcon) px += " pe-12";
  if (!startIcon && !endIcon) px = "px-4";

  return (
    <div>
      <label className="text-text-base mb-2 block text-sm font-medium">
        {label}
      </label>
      <div className="relative">
        {startIcon && (
          <span className="text-text-subtle absolute top-1/2 left-3 flex h-5 w-5 -translate-y-1/2 items-center justify-center">
            {startIcon}
          </span>
        )}
        <input
          className={`border-border bg-surface-overlay text-text-base placeholder:text-text-subtle w-full rounded-lg border outline-none ${px} ${error ? "ring-danger" : "ring-primary/90"} py-3 transition-all focus:ring-2`}
          {...attrs}
        />
        {endIcon && (
          <span className="text-text-subtle absolute top-1/2 right-3 flex h-5 w-5 -translate-y-1/2 cursor-pointer items-center justify-center">
            {endIcon}
          </span>
        )}
      </div>
      {error && <ErrorMessage message={error} />}
    </div>
  );
}

export default FormInput;
