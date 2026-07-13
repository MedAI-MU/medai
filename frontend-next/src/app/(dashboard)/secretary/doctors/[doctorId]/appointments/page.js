import { getDoctorById } from "@/services/server/doctors";
import { getDoctorAppointments } from "@/services/server/appointments";

import BackButton from "@/components/ui/BackButton";
import Heading from "@/components/ui/Heading";
import DoctorAppointmentsManagement from "@/components/doctor/DoctorAppointmentsManagement";
import EmptyState from "@/components/ui/EmptyState";

export async function generateMetadata({ params }) {
  const { doctorId } = await params;
  try {
    const doctor = await getDoctorById(doctorId);
    return {
      title: `${doctor?.name || "Doctor"}'s Appointments`,
      description: "View and manage all appointments for this doctor.",
    };
  } catch {
    return { title: "Appointments" };
  }
}

async function DoctorAppointmentsPage({ params }) {
  const { doctorId } = await params;
  const numericId = Number(doctorId);

  const [doctorAppointments, doctor] = await Promise.all([
    getDoctorAppointments(numericId),
    getDoctorById(numericId),
  ]);

  if (!doctorAppointments?.length)
    return (
      <>
        <BackButton title="Back to Doctor" />
        <EmptyState title={`DR. ${doctor?.name} has no appointments.`} />
      </>
    );

  return (
    <div className="space-y-8">
      <BackButton title="Back to Doctor" />
      <Heading
        title={`${doctor?.name || "Doctor"}'s Appointments`}
        subtitle="View and manage all appointments for this doctor."
      />

      <DoctorAppointmentsManagement
        appointments={doctorAppointments}
        doctorId={numericId}
      />
    </div>
  );
}

export default DoctorAppointmentsPage;
