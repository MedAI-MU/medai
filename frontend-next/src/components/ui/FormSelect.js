import ErrorMessage from "./ErrorMessage";

function FormSelect({ label, options, error, startIcon, ...attrs }) {
  return (
    <div>
      <label className={`text-primary-dark" mb-2 block text-sm font-medium`}>
        {label}
      </label>
      <div className="relative">
        {startIcon && (
          <span className="absolute top-1/2 left-3 flex h-5 w-5 -translate-y-1/2 items-center justify-center text-gray-400">
            {startIcon}
          </span>
        )}
        <select
          className={`border-border-gray w-full cursor-pointer rounded-lg border capitalize outline-none ${startIcon ? "ps-10 pe-4" : "px-4"} ${error ? "ring-red-400" : "ring-primary-blue/90"} py-3 transition-all focus:ring-2`}
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
