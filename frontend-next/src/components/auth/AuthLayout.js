function AuthLayout({ hero, children }) {
  return (
    <main className="flex min-h-screen">
      <div className="bg-primary-blue relative hidden flex-col justify-between gap-16 p-12 text-white lg:flex lg:w-[40%]">
        {hero}
      </div>
      <div className="flex w-full items-center justify-center overflow-y-auto p-6 sm:p-12 lg:w-[60%]">
        {children}
      </div>
    </main>
  );
}

export default AuthLayout;
