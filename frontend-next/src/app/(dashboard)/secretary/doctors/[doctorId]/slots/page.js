import { Suspense } from "react";
import { notFound } from "next/navigation";

import { getDoctorById } from "@/services/server/doctors";
import Heading from "@/components/ui/Heading";
import BackButton from "@/components/ui/BackButton";
import CreateScheduleSlots from "@/components/schedule/CreateScheduleSlots";
import ScheduleControls from "@/components/schedule/ScheduleControls";
import ScheduleSlots from "@/components/schedule/ScheduleSlots";
import ScheduleGridSkeleton from "@/components/schedule/ScheduleGridSkeleton";
import { DoctorInfoProvider } from "@/contexts/DoctorInfoContext";

export async function generateMetadata({ params }) {
  const { doctorId } = await params;
  try {
    const doctor = await getDoctorById(doctorId);
    return {
      title: `${doctor?.name || "Doctor"}'s Schedule`,
      description: "Manage weekly appointments and available time slots.",
    };
  } catch {
    return { title: "Schedule" };
  }
}

async function ScheduleSlotsPage({ searchParams, params }) {
  const { startDate } = (await searchParams) || {};
  const doctorId = Number((await params)?.doctorId);

  let doctor;
  try {
    doctor = await getDoctorById(doctorId);
  } catch (err) {
    if (err.statusCode === 404) notFound();
    throw err;
  }

  return (
    <DoctorInfoProvider doctorInfo={{ doctorId }}>
      <div className="space-y-8">
        <BackButton title="Back to Doctor" />

        <Heading
          title={`${doctor.name || "Doctor"}'s Schedule`}
          subtitle="Manage weekly appointments and available time slots."
          hideSubtitleOnMobile
          rowOnMobile
        >
          <CreateScheduleSlots />
        </Heading>

        <ScheduleControls />

        <Suspense key={startDate} fallback={<ScheduleGridSkeleton />}>
          <ScheduleSlots doctorId={doctorId} startDate={startDate} />
        </Suspense>
      </div>
    </DoctorInfoProvider>
  );
}

export default ScheduleSlotsPage;
