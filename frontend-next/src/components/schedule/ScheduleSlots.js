import { endOfWeek, parse, startOfWeek } from "date-fns";
import { getScheduleSlots } from "@/services/server/schedule";
import { formatDate } from "@/lib/utils/DateTimeHelpers";

import ErrorState from "@/components/ui/ErrorState";
import SlotsWeekView from "./SlotsWeekView";

async function ScheduleSlots({ doctorId, startDate }) {
  // If startDate is not specified, default to start of current week formatted as YYYY-MM-DD
  const baseStartDate = startDate || formatDate(startOfWeek(new Date()));
  const baseEndDate = formatDate(
    endOfWeek(parse(baseStartDate, "yyyy-MM-dd", new Date())),
  );

  let data = {};

  try {
    data = await getScheduleSlots(doctorId, baseStartDate, baseEndDate);
  } catch (err) {
    console.error("Error loading schedule slots:", err);
    return (
      <div className="flex flex-col gap-6">
        <ErrorState description="We couldn't load your schedule slots right now. Please check your connection or try again in a moment." />
      </div>
    );
  }

  const slotsData = data?.days?.data || [];

  return <SlotsWeekView slotsData={slotsData} startDate={baseStartDate} />;
}

export default ScheduleSlots;
