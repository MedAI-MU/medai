import { format } from "date-fns";
import Card from "./Card";
import Heading from "./Heading";

function TimelineCard({ title, description, dateDescription, date, children }) {
  return (
    <Card className="relative flex flex-col gap-6">
      <Heading Tag="h3" size="sm" title={title} subtitle={description} />
      <p className="text-text-muted text-sm italic">
        <span>{dateDescription} </span>
        <span className="font-bold">{format(date, "MMMM dd, yyyy")}</span>
      </p>

      {/* Mostly action buttons */}
      {children}
    </Card>
  );
}

export default TimelineCard;
