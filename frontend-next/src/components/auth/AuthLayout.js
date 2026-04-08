function AuthLayout({ hero, children }) {
  return (
    <main className="bg-surface-bg flex min-h-screen">
      {/* Left — hero */}
      <div className="bg-primary/95 dark:bg-primary/20 relative hidden flex-col justify-between gap-16 overflow-hidden p-12 text-white lg:flex lg:w-[40%]">
        <div className="from-primary via-primary/90 absolute inset-0 bg-linear-to-br to-[#2c3e50] opacity-50 dark:opacity-30" />
        <div className="relative z-10 flex h-full flex-col justify-between gap-16">
          {hero}
        </div>
      </div>
      <div className="flex w-full items-center justify-center overflow-y-auto p-6 sm:p-12 lg:w-[60%]">
        {children}
      </div>
    </main>
  );
}

export default AuthLayout;
