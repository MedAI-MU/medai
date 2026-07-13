import AuthLayout from "@/components/auth/AuthLayout";
import PendingHero from "@/components/auth/PendingHero";
import PendingApprovalContent from "@/components/auth/PendingApprovalContent";
import { getUserFromToken } from "@/lib/session";

export const metadata = {
  title: "Pending Approval",
  description: "Your account is pending approval from a manager.",
};

async function PendingApprovalPage() {
  const user = await getUserFromToken();

  return (
    <AuthLayout hero={<PendingHero />}>
      <div className="border-border bg-surface relative w-full max-w-2xl rounded-2xl border p-8 shadow-xl sm:p-10">
        <PendingApprovalContent user={user} />
      </div>
    </AuthLayout>
  );
}

export default PendingApprovalPage;
