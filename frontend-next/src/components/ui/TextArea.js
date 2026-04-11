import ErrorMessage from "./ErrorMessage";

function TextArea({ label, rows, error, ...attrs }) {
  return (
    <div>
      <label className="text-text-base mb-2 block text-sm font-medium">
        {label}
      </label>
      <textarea
        className={`border-border text-text-base bg-surface placeholder:text-text-subtle block w-full resize-none rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2 ${error ? "ring-danger" : "ring-primary/90"}`}
        rows={rows}
        {...attrs}
      />
      {error && <ErrorMessage message={error} />}
    </div>
  );
}

export default TextArea;
