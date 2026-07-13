import AuthLayout from "@/components/auth/AuthLayout";
import FormSection from "@/components/auth/FormSection";
import LoginForm from "@/components/auth/LoginForm";
import LoginHero from "@/components/auth/LoginHero";

export const metadata = {
  title: "Sign In",
  description: "Please enter your credentials to access your account.",
};

async function LoginPage() {
  return (
    <AuthLayout hero={<LoginHero />}>
      <FormSection
        title="Sign In"
        subTitle="Please enter your credentials to access your account."
        footer={{
          text: "Don't have an account?",
          href: "/auth/signup",
          action: "Sign up",
        }}
      >
        <LoginForm />
      </FormSection>
    </AuthLayout>
  );
}

export default LoginPage;
