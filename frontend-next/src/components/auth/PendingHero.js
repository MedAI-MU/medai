import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function PendingHero() {
  return (
    <>
      <Logo className="relative z-10" />
      <div className="relative z-10">
        <SectionHeader
          className="mb-10"
          variant="hero"
          title={
            <>
              Account <br /> Under Review
            </>
          }
          subTitle="We appreciate your patience while our team reviews your registration. You will gain access once your account is approved."
        />
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-lg shadow-2xl">
          <div className="mb-4 flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center rounded-full border-2 border-amber-400/40 bg-amber-400/10">
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
                className="text-amber-400"
              >
                <path d="M5 22h14" />
                <path d="M5 2h14" />
                <path d="M17 22v-4.172a2 2 0 0 0-.586-1.414L12 12l-4.414 4.414A2 2 0 0 0 7 17.828V22" />
                <path d="M7 2v4.172a2 2 0 0 0 .586 1.414L12 12l4.414-4.414A2 2 0 0 0 17 6.172V2" />
              </svg>
            </div>
            <div>
              <p className="text-base font-semibold tracking-tight">
                Pending Approval
              </p>
              <p className="text-sm text-white/60">
                Verification in progress
              </p>
            </div>
          </div>
          <p className="text-[17px] leading-relaxed text-white/90 italic">
            &quot;New accounts are reviewed by an administrator before access is
            granted. This ensures a secure environment for all users.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default PendingHero;
