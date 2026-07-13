import { Suspense } from "react";
import { getUserFromToken } from "@/lib/session";

import Heading from "@/components/ui/Heading";
import CreateScheduleSlots from "@/components/schedule/CreateScheduleSlots";
import ScheduleControls from "@/components/schedule/ScheduleControls";
import ScheduleSlots from "@/components/schedule/ScheduleSlots";
import ScheduleGridSkeleton from "@/components/schedule/ScheduleGridSkeleton";
import { DoctorInfoProvider } from "@/contexts/DoctorInfoContext";

export const metadata = {
  title: "Schedule",
  description: "Manage your weekly appointments and availability.",
};

async function AvailabilityPage({ searchParams }) {
  const { sub: doctorId, email } = (await getUserFromToken()) || {};
  const { startDate } = (await searchParams) || {};

  return (
    <DoctorInfoProvider doctorInfo={{ doctorId, email }}>
      <div className="space-y-8">
        <Heading
          title="Schedule"
          subtitle="Manage your weekly appointments"
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

export default AvailabilityPage;
