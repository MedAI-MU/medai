function TextArea({ label, rows, error, ...attrs }) {
  return (
    <div>
      <label className="text-primary-dark mb-2 block text-sm font-medium">
        {label}
      </label>
      <textarea
        className="border-border-gray w-full rounded-lg border px-4 py-3"
        rows={rows}
        {...attrs}
      />
    </div>
  );
}

export default TextArea;
