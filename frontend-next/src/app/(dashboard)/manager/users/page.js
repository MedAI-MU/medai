import {
  getSecretaries,
  getPendingSecretaries,
} from "@/services/server/manager";
import Heading from "@/components/ui/Heading";
import SecretariesManager from "@/components/manager/SecretariesManager";

export const metadata = {
  title: "Users",
  description: "Manage all users in the system.",
};

export default async function UsersPage() {
  const [secretaries, pending] = await Promise.all([
    getSecretaries(),
    getPendingSecretaries(),
  ]);

  return (
    <div className="space-y-6">
      <Heading title="Users" subtitle="Manage all users in the system." />
      <SecretariesManager
        approved={secretaries ?? []}
        pending={pending ?? []}
      />
    </div>
  );
}
