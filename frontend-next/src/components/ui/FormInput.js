function FormInput({ label, error, startIcon, endIcon, ...attrs }) {
  let px = "";
  if (startIcon) px += "ps-10";
  if (endIcon) px += " pe-12";
  if (!startIcon && !endIcon) px = "px-4";

  return (
    <div>
      <label className="text-primary-dark mb-2 block text-sm font-medium">
        {label}
      </label>
      <div className="relative">
        {startIcon && (
          <span className="absolute top-1/2 left-3 flex h-5 w-5 -translate-y-1/2 items-center justify-center text-gray-400">
            {startIcon}
          </span>
        )}
        <input
          className={`border-border-gray w-full rounded-lg border outline-none ${px} ring-primary-blue/90 py-3 transition-all focus:ring-2`}
          {...attrs}
        />
        {endIcon && (
          <span className="absolute top-1/2 right-3 flex h-5 w-5 -translate-y-1/2 cursor-pointer items-center justify-center text-gray-400">
            {endIcon}
          </span>
        )}
      </div>
    </div>
  );
}

export default FormInput;
