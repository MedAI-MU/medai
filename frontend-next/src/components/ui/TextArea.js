function TextArea({ label, rows, error, ...attrs }) {
  return (
    <div>
      <label className="text-text-base mb-2 block text-sm font-medium">
        {label}
      </label>
      <textarea
        className="border-border text-text-base bg-surface-overlay placeholder:text-text-subtle ring-primary/90 w-full resize-none rounded-lg border px-4 py-3 transition-all outline-none focus:ring-2"
        rows={rows}
        {...attrs}
      />
    </div>
  );
}

export default TextArea;
