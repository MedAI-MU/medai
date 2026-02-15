import SignupImage from "@/assets/signup.png";
import Image from "next/image";
import AuthLayout from "@/components/auth/AuthLayout";
import Logo from "@/components/ui/Logo";
import SectionHeader from "@/components/ui/SectionHeader";
import FormSection from "@/components/auth/FormSection";
import SignupForm from "@/components/auth/SignupForm";

function SignupPage() {
  return (
    <AuthLayout
      hero={
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
        </>
      }
      form={
        <FormSection
          title="Get Started"
          subTitle="Join our network of healthcare professionals and patients."
          footer={{
            text: "Already have an account?",
            href: "/auth/login",
            action: "Log in",
          }}
        >
          <SignupForm />
        </FormSection>
      }
    />
  );
}

export default SignupPage;
