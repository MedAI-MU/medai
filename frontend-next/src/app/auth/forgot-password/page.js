import AuthLayout from "@/components/auth/AuthLayout";
import FormSection from "@/components/auth/FormSection";
import ForgotPasswordForm from "@/components/auth/ForgotPasswordForm";
import ForgotPasswordHero from "@/components/auth/ForgotPasswordHero";

export const metadata = {
  title: "Forgot Password",
  description: "Enter your email to receive a reset link.",
};

function ForgotPasswordPage() {
  return (
    <AuthLayout hero={<ForgotPasswordHero />}>
      <FormSection
        title="Forgot Password"
        subTitle="Enter your email to receive a reset link."
        footer={{
          text: "Remember your password?",
          href: "/auth/login",
          action: "Sign in",
        }}
      >
        <ForgotPasswordForm />
      </FormSection>
    </AuthLayout>
  );
}

export default ForgotPasswordPage;
