import Card from "@/components/ui/Card";
import Heading from "@/components/ui/Heading";
import { format } from "date-fns";
import CardActions from "./CardActions";

function TimelineItem({
  title,
  description,
  date,
  dateDescription = "",
  editTitle,
  editDescription,
  editForm,
  onDelete,
  deleteSuccessMessage,
  deleteFailMessage,
}) {
  return (
    <div className="relative grid gap-(--timeline-item-gap-mobile) ps-(--timeline-pd-start) sm:grid-cols-[var(--timeline-item-date-width)_1fr] sm:gap-(--timeline-item-gap-desktop)">
      <div className="bg-surface absolute top-0 left-0 flex size-4 -translate-x-1/2 items-center justify-center rounded-full sm:left-(--timeline-line-start-desktop)">
        <span className="bg-primary size-2 rounded-full" />
      </div>
      <div className="flex sm:justify-end">
        <span className="font-bold">{format(date, "MMM, yyyy")}</span>
      </div>
      <Card className="relative flex flex-col gap-6">
        <Heading Tag="h3" size="sm" title={title} subtitle={description} />
        <p className="text-text-muted text-sm italic">
          <span>{dateDescription} </span>
          <span className="font-bold">{format(date, "MMMM dd, yyyy")}</span>
        </p>
        {(editForm || onDelete) && (
          <CardActions
            editTitle={editTitle}
            editDescription={editDescription}
            editForm={editForm}
            deleteSuccessMessage={deleteSuccessMessage}
            deleteFailMessage={deleteFailMessage}
            onConfirmDelete={onDelete}
          />
        )}
      </Card>
    </div>
  );
}

export default TimelineItem;
