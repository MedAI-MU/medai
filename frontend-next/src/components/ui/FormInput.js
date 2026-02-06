function FormInput({ label, error, ...attrs }) {
  return (
    <div>
      <label className="text-primary-dark mb-2 block text-sm font-medium">
        {label}
      </label>
      <input
        {...attrs}
        className="border-border-gray w-full rounded-lg border px-4 py-3"
      />
    </div>
  );
}

export default FormInput;
