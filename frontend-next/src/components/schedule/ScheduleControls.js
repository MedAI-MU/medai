import WeekNavigator from "./WeekNavigator";
import ScheduleLegend from "./ScheduleLegend";

export default function ScheduleControls() {
  return (
    <div className="border-border bg-surface flex items-center justify-between rounded-lg border px-6 py-4 shadow-sm max-md:justify-center">
      <WeekNavigator />
      <ScheduleLegend />
    </div>
  );
}
