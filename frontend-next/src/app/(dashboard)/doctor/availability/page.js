import { Suspense } from "react";
import { getUserFromToken } from "@/lib/session";

import Heading from "@/components/ui/Heading";
import AddSlots from "@/components/schedule/AddSlots";
import ScheduleControls from "@/components/schedule/ScheduleControls";
import ScheduleSlots from "@/components/schedule/ScheduleSlots";
import ScheduleGridSkeleton from "@/components/schedule/ScheduleGridSkeleton";
import { DoctorInfoProvider } from "@/contexts/DoctorInfoContext";

async function AvailabilityPage({ searchParams }) {
  const { sub: doctorId, email } = (await getUserFromToken()) || {};
  const { startDate } = (await searchParams) || {};

  return (
    <div className="space-y-8">
      <Heading
        title="Schedule"
        subtitle="Manage your weekly appointments"
        hideSubtitleOnMobile
        rowOnMobile
      >
        <AddSlots />
      </Heading>

      <ScheduleControls />

      {/* Context to reuse ScheduleSlots in more than one role-page as doctor info is needed */}
      <DoctorInfoProvider doctorInfo={{ doctorId, email }}>
        <Suspense key={startDate} fallback={<ScheduleGridSkeleton />}>
          <ScheduleSlots doctorId={doctorId} startDate={startDate} />
        </Suspense>
      </DoctorInfoProvider>
    </div>
  );
}

export default AvailabilityPage;
