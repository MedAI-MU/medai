import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function VerifyEmailHero() {
  return (
    <>
      <Logo className="relative z-10" />
      <div className="relative z-10">
        <SectionHeader
          className="mb-10"
          variant="hero"
          title={
            <>
              Verify Your <br /> Email
            </>
          }
          subTitle="Confirm your email address to activate your account and gain full access to MedAI."
        />
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-lg shadow-2xl">
          <div className="mb-4 flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-sky-400/40 bg-sky-400/10">
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
                className="text-sky-400"
              >
                <path d="M22 17a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V9.5C2 7 4 5 6.5 5H18c2.2 0 4 1.8 4 4v8Z" />
                <polyline points="15,9 18,9 18,12" />
                <path d="M6.5 5C9 5 11 7 11 9.5V17a2 2 0 0 1-2 2v0" />
                <line x1="6" x2="7" y1="10" y2="10" />
              </svg>
            </div>
            <div>
              <p className="text-base font-semibold tracking-tight">
                Email Verification
              </p>
              <p className="text-sm text-white/60">
                One-time confirmation
              </p>
            </div>
          </div>
          <p className="text-[17px] leading-relaxed text-white/90 italic">
            &quot;Verifying your email helps us keep your account secure and
            ensures you never miss important updates.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default VerifyEmailHero;
