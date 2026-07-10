import { getManagers } from "@/services/server/manager";
import Heading from "@/components/ui/Heading";
import ManagersList from "@/components/manager/ManagersList";

export default async function ManagersPage() {
  const managers = await getManagers();

  return (
    <div className="space-y-6">
      <Heading
        title="Managers"
        subtitle="View all manager accounts in the system."
      />
      <ManagersList managers={managers ?? []} />
    </div>
  );
}
