import SignupImage from "@/assets/signup.png";
import Image from "next/image";
import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import Copyright from "@/components/ui/Copyright";

function SignupHero() {
  return (
    <>
      <div>
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
        className="mx-auto w-full max-w-sm rounded-xl opacity-80"
        alt="sign up hero image"
        placeholder="blur"
      />
      <Copyright className="text-white/70" />
    </>
  );
}

export default SignupHero;
