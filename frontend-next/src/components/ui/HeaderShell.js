function HeaderShell({ children, className }) {
  return (
    <header
      className={`border-border bg-surface/80 z-30 flex h-(--header-height) items-center border-b py-4 shadow-sm backdrop-blur-md transition-colors duration-300 ${className}`}
    >
      {children}
    </header>
  );
}

export default HeaderShell;
