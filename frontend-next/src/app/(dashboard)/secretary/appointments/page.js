import { getAllAppointments } from "@/services/server/appointments";
import Heading from "@/components/ui/Heading";
import EmptyState from "@/components/ui/EmptyState";
import AllAppointmentsList from "@/components/secretary/AllAppointmentsList";

export default async function SecretaryAppointmentsPage() {
  const appointments = await getAllAppointments();

  return (
    <div className="space-y-8">
      <Heading
        title="Appointments"
        subtitle="View all appointments across all doctors."
      />

      {appointments?.length > 0 ? (
        <AllAppointmentsList appointments={appointments} />
      ) : (
        <EmptyState title="There are no appointments yet." />
      )}
    </div>
  );
}
