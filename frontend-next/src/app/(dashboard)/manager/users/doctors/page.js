import { getDoctors, getPendingDoctors } from "@/services/server/manager";
import Heading from "@/components/ui/Heading";
import DoctorsManager from "@/components/manager/DoctorsManager";

export const metadata = {
  title: "Doctors",
  description: "Manage all doctor accounts in the system.",
};

export default async function DoctorsPage() {
  const [doctors, pending] = await Promise.all([
    getDoctors(),
    getPendingDoctors(),
  ]);

  return (
    <div className="space-y-6">
      <Heading
        title="Doctors"
        subtitle="Manage all doctor accounts in the system."
      />
      <DoctorsManager approved={doctors ?? []} pending={pending ?? []} />
    </div>
  );
}
