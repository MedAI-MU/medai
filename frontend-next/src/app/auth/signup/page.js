import AuthLayout from "@/components/auth/AuthLayout";
import FormSection from "@/components/auth/FormSection";
import SignupForm from "@/components/auth/SignupForm";
import SignupHero from "@/components/auth/SignupHero";

export const metadata = {
  title: "Sign Up",
  description: "Join our network of healthcare professionals and patients.",
};

function SignupPage() {
  return (
    <AuthLayout hero={<SignupHero />}>
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
    </AuthLayout>
  );
}

export default SignupPage;
