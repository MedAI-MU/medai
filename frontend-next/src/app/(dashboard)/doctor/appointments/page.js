import DoctorAppointments from "@/components/doctor/DoctorAppointments";
import EmptyState from "@/components/ui/EmptyState";
import Heading from "@/components/ui/Heading";
import { getUserAppointments } from "@/services/server/appointments";

export const metadata = {
  title: "My Appointments",
  description: "View and manage your patient appointments schedule.",
};

async function DoctorAppointmentsPage() {
  const data = await getUserAppointments();

  if (!data?.length)
    return <EmptyState title="You have no appointments yet." />;

  return (
    <div className="space-y-8">
      <Heading
        title="My Appointments"
        subtitle="View and manage your patient appointments schedule."
      />
      <DoctorAppointments appointments={data} />
    </div>
  );
}

export default DoctorAppointmentsPage;
