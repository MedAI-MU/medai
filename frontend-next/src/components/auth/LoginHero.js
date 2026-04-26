import doctorLogo from "@/assets/doctor-logo.png";
import Image from "next/image";
import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function LoginHero() {
  return (
    <>
      <Logo />
      <div>
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
        <div className="rounded-xl border border-white/20 bg-white/10 p-6 backdrop-blur-md">
          <div className="mb-4 flex items-center gap-4">
            <Image
              src={doctorLogo}
              alt="Doctor profile"
              className="h-12 w-12 rounded-full border-2 border-white/50 object-cover"
              data-alt="Close up portrait of a professional doctor smiling"
            />
            <div>
              <p className="text-sm font-medium">Dr. Sarah Jenkins</p>
              <p className="text-xs text-white/70">Cardiology Department</p>
            </div>
          </div>
          <p className="text-sm text-white/90 italic">
            &quot;The AI assistance has revolutionized how we process diagnostic
            imagery, giving us more time for patient care.&quot;
          </p>
        </div>
      </div>
      <Copyright className="text-white/70" />
    </>
  );
}

export default LoginHero;
