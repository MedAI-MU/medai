function SheetForm({ onSubmit, children }) {
  return (
    <form
      className="flex h-full w-full flex-col overflow-hidden"
      onSubmit={onSubmit}
    >
      {children}
    </form>
  );
}

export default SheetForm;
