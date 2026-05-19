function FormLabel({ label, required = false }) {
  return (
    <label className="text-text-base mb-2 block text-sm font-medium">
      {label} {required && <span className="text-danger">*</span>}
    </label>
  );
}

export default FormLabel;
