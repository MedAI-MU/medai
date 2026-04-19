import SignupImage from "@/assets/signup.png";
import Image from "next/image";
import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function SignupHero() {
  return (
    <>
      <div className="relative z-10">
        <Logo />
        <SectionHeader
          className="mt-10"
          variant="hero"
          title="Join MedAI Community"
          subTitle=" Experience the future of integrated healthcare with AI-driven insights and seamless patient-doctor connectivity."
        />
      </div>
      <Image
        src={SignupImage}
        className="relative z-10 mx-auto w-full max-w-sm rounded-2xl border border-white/10 opacity-90 shadow-2xl"
        alt="sign up hero image"
        placeholder="blur"
      />
      <Copyright className="text-white/40 relative z-10 text-sm" />
    </>
  );
}

export default SignupHero;
