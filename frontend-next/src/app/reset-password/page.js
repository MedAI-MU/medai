import AuthLayout from "@/components/auth/AuthLayout";
import FormSection from "@/components/auth/FormSection";
import ResetPasswordForm from "@/components/auth/ResetPasswordForm";
import ResetPasswordHero from "@/components/auth/ResetPasswordHero";

function ResetPasswordPage() {
  return (
    <AuthLayout hero={<ResetPasswordHero />}>
      <FormSection
        title="Reset Password"
        subTitle="Enter your new password below."
        footer={{
          text: "Back to",
          href: "/auth/login",
          action: "Sign in",
        }}
      >
        <ResetPasswordForm />
      </FormSection>
    </AuthLayout>
  );
}

export default ResetPasswordPage;
