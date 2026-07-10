import {
  getSecretaries,
  getPendingSecretaries,
} from "@/services/server/manager";
import Heading from "@/components/ui/Heading";
import SecretariesManager from "@/components/manager/SecretariesManager";

export default async function SecretariesPage() {
  const [secretaries, pending] = await Promise.all([
    getSecretaries(),
    getPendingSecretaries(),
  ]);

  return (
    <div className="space-y-6">
      <Heading
        title="Secretaries"
        subtitle="Manage all secretary accounts in the system."
      />
      <SecretariesManager
        approved={secretaries ?? []}
        pending={pending ?? []}
      />
    </div>
  );
}
