function DialogBody({ children }) {
  return (
    <div className="relative">
      <div className="no-scrollbar relative flex max-h-[60vh] flex-col gap-6 overflow-y-auto px-1 py-6">
        {children}
      </div>
      <div className="from-surface pointer-events-none absolute inset-x-0 -top-0.5 z-10 h-6 bg-linear-to-b to-transparent" />
      <div className="from-surface pointer-events-none absolute inset-x-0 -bottom-0.5 z-10 h-6 bg-linear-to-t to-transparent" />
    </div>
  );
}

export default DialogBody;
