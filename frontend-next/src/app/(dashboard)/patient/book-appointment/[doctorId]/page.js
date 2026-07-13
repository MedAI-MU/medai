import { notFound } from "next/navigation";
import { getDoctorById } from "@/services/server/doctors";
import { getScheduleSlots } from "@/services/server/schedule";
import { DoctorInfoProvider } from "@/contexts/DoctorInfoContext";
import { formatDate } from "@/lib/utils/DateTimeHelpers";
import { PAGE_SIZE } from "@/constants/pagination";

import BackButton from "@/components/ui/BackButton";
import ErrorState from "@/components/ui/ErrorState";
import EmptyState from "@/components/ui/EmptyState";
import DoctorProfileCard from "@/components/doctor/DoctorProfileCard";
import DayCarouselWrapper from "@/components/appointments/DayCarouselWrapper";

export async function generateMetadata({ params }) {
  const { doctorId } = await params;
  try {
    const doctor = await getDoctorById(doctorId);
    return {
      title: `Book Appointment - ${doctor?.name || "Doctor"}`,
      description: "Select a date and time slot for your visit.",
    };
  } catch {
    return { title: "Book Appointment" };
  }
}

async function DoctorSlotsPage({ params, searchParams }) {
  const { doctorId } = await params;
  const { pageNo = "1" } = await searchParams;

  const today = formatDate(new Date());
  let data;
  try {
    data = await getScheduleSlots({
      doctorId,
      fromDate: today,
      pageNo: Number(pageNo),
      pageSize: PAGE_SIZE,
    });
  } catch (err) {
    console.error("Failed to load doctor slots", err);
    if (err?.statusCode === 404) notFound("hello ");

    return (
      <div className="space-y-6">
        <BackButton title="Back to Doctors" />
        <ErrorState
          description="We couldn't load the slots right now. Please check your connection
        or try again in a moment."
        />
      </div>
    );
  }

  const hasDays = data?.days?.data?.length > 0;

  return (
    <DoctorInfoProvider
      doctorInfo={{ doctorId, name: data?.name, speciality: data?.speciality }}
    >
      <div className="space-y-6">
        <BackButton title="Back to Doctors" />

        <DoctorProfileCard name={data?.name} speciality={data?.speciality} />

        {hasDays && (
          <DayCarouselWrapper
            key={pageNo}
            days={data?.days}
            doctorId={doctorId}
            doctorName={data?.name}
          />
        )}
        {!hasDays && (
          <EmptyState
            title="No slots found"
            description={
              <>
                Dr. <strong className="capitalize">{data?.name} </strong>{" "}
                didn&apos;t add slots yet.
              </>
            }
          />
        )}
      </div>
    </DoctorInfoProvider>
  );
}

export default DoctorSlotsPage;
