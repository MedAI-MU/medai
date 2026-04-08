import doctorLogo from "@/assets/doctor-logo.png";
import Image from "next/image";
import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function LoginHero() {
  return (
    <>
      <Logo className="relative z-10" />
      <div className="relative z-10">
        <SectionHeader
          className="mb-10"
          variant="hero"
          title={
            <>
              Welcome Back <br /> to MedAI
            </>
          }
          subTitle="Securely access your clinical insights, patient data, and AI-driven diagnostic tools in one unified workspace."
        />
        <div className="rounded-2xl border border-white/10 bg-white/5 p-6 backdrop-blur-lg shadow-2xl">
          <div className="mb-4 flex items-center gap-4">
            <Image
              src={doctorLogo}
              alt="Doctor profile"
              className="h-14 w-14 rounded-full border-2 border-white/30 object-cover shadow-sm"
              data-alt="Close up portrait of a professional doctor smiling"
            />
            <div>
              <p className="text-base font-semibold tracking-tight">Dr. Sarah Jenkins</p>
              <p className="text-sm text-white/60">Cardiology Department</p>
            </div>
          </div>
          <p className="text-[17px] leading-relaxed text-white/90 italic">
            &quot;The AI assistance has revolutionized how we process diagnostic
            imagery, giving us more time for patient care.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default LoginHero;
