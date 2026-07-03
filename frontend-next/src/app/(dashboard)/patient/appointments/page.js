import PatientAppointments from "@/components/patient/PatientAppointments";
import EmptyState from "@/components/ui/EmptyState";
import Heading from "@/components/ui/Heading";
import { getUserAppointments } from "@/services/server/appointments";

async function PatientAppointmentsPage() {
  const data = await getUserAppointments();

  if (!data?.length)
    return <EmptyState title="You have no appointments yet." />;

  return (
    <div className="space-y-8">
      <Heading
        title="My Appointments"
        subtitle="Track and manage your scheduled healthcare visits."
      />
      <PatientAppointments appointments={data} />
    </div>
  );
}

export default PatientAppointmentsPage;
