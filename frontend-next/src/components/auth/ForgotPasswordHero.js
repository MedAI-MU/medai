import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function ForgotPasswordHero() {
  return (
    <>
      <Logo className="relative z-10" />
      <div className="relative z-10">
        <SectionHeader
          className="mb-10"
          variant="hero"
          title={
            <>
              Reset Your <br /> Password
            </>
          }
          subTitle="Enter your email address and we'll send you a link to reset your password securely."
        />
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-lg shadow-2xl">
          <div className="mb-4 flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-blue-400/40 bg-blue-400/10">
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
                className="text-blue-400"
              >
                <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
                <path d="M7 11V7a5 5 0 0 1 10 0v4" />
              </svg>
            </div>
            <div>
              <p className="text-base font-semibold tracking-tight">
                Secure Reset
              </p>
              <p className="text-sm text-white/60">
                Link expires in 1 hour
              </p>
            </div>
          </div>
          <p className="text-[17px] leading-relaxed text-white/90 italic">
            &quot;We take security seriously. Your reset link will expire after
            one hour for your protection.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default ForgotPasswordHero;
