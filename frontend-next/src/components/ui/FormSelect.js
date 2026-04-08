import ErrorMessage from "./ErrorMessage";

function FormSelect({ label, options, error, startIcon, ...attrs }) {
  return (
    <div>
      <label className={`text-text-base mb-2 block text-sm font-medium`}>
        {label}
      </label>
      <div className="relative">
        {startIcon && (
          <span className="text-text-subtle absolute top-1/2 left-3 flex h-5 w-5 -translate-y-1/2 items-center justify-center">
            {startIcon}
          </span>
        )}
        <select
          className={`border-border bg-surface-overlay text-text-base w-full cursor-pointer rounded-lg border capitalize outline-none ${startIcon ? "ps-10 pe-4" : "px-4"} ${error ? "ring-danger" : "ring-primary/90"} py-3 transition-all focus:ring-2`}
          {...attrs}
        >
          <option value="" disabled>
            Select role...
          </option>
          {options.map((option) => (
            <option key={option} value={option}>
              {option}
            </option>
          ))}
        </select>
      </div>
      {error && <ErrorMessage message={error} />}
    </div>
  );
}

export default FormSelect;
