import { CalendarCheck, Clock, FileText, Stethoscope } from "lucide-react";
import { notFound } from "next/navigation";

import { getDoctorById } from "@/services/server/doctors";
import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import DoctorProfileCard from "@/components/doctor/DoctorProfileCard";
import DoctorActionCard from "@/components/doctor/DoctorActionCard";
import Grid from "@/components/ui/Grid";

export async function generateMetadata({ params }) {
  const { doctorId } = await params;
  try {
    const doctor = await getDoctorById(doctorId);
    return {
      title: `${doctor?.name || "Doctor"} - Management`,
      description: "Manage the doctor's specialities, schedule templates, slots, and appointments.",
    };
  } catch {
    return { title: "Doctor Management" };
  }
}

const actions = [
  {
    icon: Stethoscope,
    title: "Specialities",
    description:
      "Manage the doctor's medical specialities. Add or remove their areas of expertise.",
    actionLabel: "Manage Specialities",
    getHref: (doctorId) => `/secretary/doctors/${doctorId}/specialities`,
  },
  {
    icon: FileText,
    title: "Schedule Templates",
    description:
      "Create and manage weekly recurring time templates. Apply them to generate schedule slots.",
    actionLabel: "Manage Templates",
    getHref: (doctorId) => `/secretary/doctors/${doctorId}/templates`,
  },
  {
    icon: Clock,
    title: "Schedule Slots",
    description:
      "View, create, edit, or delete the doctor's available time slots.",
    actionLabel: "Manage Slots",
    getHref: (doctorId) => `/secretary/doctors/${doctorId}/slots`,
  },
  {
    icon: CalendarCheck,
    title: "Appointments",
    description:
      "View all appointments for this doctor. Update status or cancel bookings.",
    actionLabel: "Manage Appointments",
    getHref: (doctorId) => `/secretary/doctors/${doctorId}/appointments`,
  },
];

async function DoctorDetailPage({ params }) {
  const { doctorId } = await params;

  let doctor;
  try {
    doctor = await getDoctorById(doctorId);
  } catch (err) {
    if (err.statusCode === 404) notFound();
    throw err;
  }

  const primarySpeciality = doctor?.specialities?.find((s) => s?.isPrimary);

  return (
    <div className="space-y-8">
      <BackButton title="Back to Doctors" />
      <div className="space-y-2">
        <Heading
          size="xl"
          title="Doctor Management"
          subtitle="Manage the doctor's specialities, schedule templates, slots, and appointments."
          hideSubtitleOnMobile
        />
      </div>

      <DoctorProfileCard
        name={doctor.name || "Unknown"}
        speciality={primarySpeciality}
      />

      <Grid cols="two">
        {actions.map((action) => (
          <DoctorActionCard
            key={action.title}
            href={action.getHref(doctorId)}
            {...action}
          />
        ))}
      </Grid>
    </div>
  );
}

export default DoctorDetailPage;
