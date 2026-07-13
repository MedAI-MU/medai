import AuthLayout from "@/components/auth/AuthLayout";
import VerifyEmailHero from "@/components/auth/VerifyEmailHero";
import VerifyEmailContent from "@/components/auth/VerifyEmailContent";

export const metadata = {
  title: "Verify Email",
  description: "Verify your email address to activate your account.",
};

function VerifyEmailPage() {
  return (
    <AuthLayout hero={<VerifyEmailHero />}>
      <div className="border-border bg-surface relative w-full max-w-2xl rounded-2xl border p-8 shadow-xl sm:p-10">
        <VerifyEmailContent />
      </div>
    </AuthLayout>
  );
}

export default VerifyEmailPage;
