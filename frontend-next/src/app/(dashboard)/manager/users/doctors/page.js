import { getDoctors, getPendingDoctors } from "@/services/server/manager";
import Heading from "@/components/ui/Heading";
import DoctorsManager from "@/components/manager/DoctorsManager";

export default async function DoctorsPage() {
  const [doctors, pending] = await Promise.all([
    getDoctors(),
    getPendingDoctors(),
  ]);
  console.log(doctors, pending);

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
