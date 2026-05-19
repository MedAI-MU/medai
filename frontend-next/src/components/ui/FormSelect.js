import ErrorMessage from "./ErrorMessage";
import FormLabel from "./FormLabel";

function FormSelect({
  label,
  defaultValue = "Select role...",
  options,
  error,
  startIcon,
  ...attrs
}) {
  return (
    <div>
      {label && <FormLabel label={label} />}
      <div className="relative">
        {startIcon && (
          <span className="text-text-subtle absolute top-1/2 left-3 flex h-5 w-5 -translate-y-1/2 items-center justify-center">
            {startIcon}
          </span>
        )}
        <select
          defaultValue=""
          className={`border-border bg-surface-overlay disabled:text-text-muted disabled:border-border text-text-base w-full cursor-pointer rounded-lg border capitalize outline-none disabled:cursor-not-allowed disabled:opacity-75 ${startIcon ? "ps-10 pe-4" : "px-4"} ${error ? "ring-danger" : "ring-primary/90"} py-3 transition-all focus:ring-2`}
          {...attrs}
        >
          <option value="" disabled>
            {defaultValue}
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
