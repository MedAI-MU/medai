import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function ResetPasswordHero() {
  return (
    <>
      <Logo className="relative z-10" />
      <div className="relative z-10">
        <SectionHeader
          className="mb-10"
          variant="hero"
          title={
            <>
              Create New <br /> Password
            </>
          }
          subTitle="Choose a strong, unique password that you haven't used before to keep your account secure."
        />
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-lg shadow-2xl">
          <div className="mb-4 flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-emerald-400/40 bg-emerald-400/10">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="28"
                height="28"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className="text-emerald-400"
              >
                <path d="M12 22s-8-4.5-8-11.8A3.2 3.2 0 0 1 7.2 7h-.2a3.2 3.2 0 0 1 3.2 3.2v.2" />
                <path d="M18 11V7a5 5 0 0 0-5-5h-2a5 5 0 0 0-5 5v4" />
                <path d="M5 11h14" />
                <rect width="18" height="10" x="3" y="11" rx="2" />
              </svg>
            </div>
            <div>
              <p className="text-base font-semibold tracking-tight">
                Strong Password
              </p>
              <p className="text-sm text-white/60">
                Min 6 characters
              </p>
            </div>
          </div>
          <p className="text-[17px] leading-relaxed text-white/90 italic">
            &quot;Use a mix of letters, numbers, and symbols to create a strong
            password that protects your account.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default ResetPasswordHero;
