import Card from "../ui/Card";
import DayCard from "./DayCard";

function AppointmentCardLayout({ date, infoSection, isCompleted, actions }) {
  return (
    <Card className="flex flex-col items-start gap-6 md:flex-row md:items-center">
      {/* <!-- Date Badge --> */}
      <DayCard date={date} isCompleted={isCompleted} />
      {/* <!-- Info Section --> */}
      <div className="flex-1 space-y-2">{infoSection}</div>
      {/* <!-- Status & Actions --> */}
      <div className="flex w-full flex-col items-end gap-4 md:w-auto">
        {actions}
      </div>
    </Card>
  );
}

export default AppointmentCardLayout;
