import ErrorMessage from "./ErrorMessage";
import FormLabel from "./FormLabel";

function TextArea({ label, rows, error, ...attrs }) {
  return (
    <div>
      {label && <FormLabel label={label} />}
      <textarea
        className={`border-border text-text-base bg-surface-overlay disabled:text-text-muted placeholder:text-text-subtle block w-full resize-none rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2 disabled:cursor-not-allowed disabled:opacity-75 ${error ? "ring-danger" : "ring-primary/90"}`}
        rows={rows}
        {...attrs}
      />
      {error && <ErrorMessage message={error} />}
    </div>
  );
}

export default TextArea;
