import PatientAppointmentCard from "@/components/patient/PatientAppointmentCard";
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
      <div className="space-y-6">
        {data?.map((appointment) => (
          <PatientAppointmentCard
            key={appointment?.id}
            appointment={appointment}
          />
        ))}
      </div>
    </div>
  );
}

export default PatientAppointmentsPage;
