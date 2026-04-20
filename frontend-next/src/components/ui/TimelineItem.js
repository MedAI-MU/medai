import { format } from "date-fns";

function TimelineItem({ date, children }) {
  return (
    <div className="relative grid gap-(--timeline-item-gap-mobile) ps-(--timeline-pd-start) sm:grid-cols-[var(--timeline-item-date-width)_1fr] sm:gap-(--timeline-item-gap-desktop)">
      <div className="bg-surface absolute top-0 left-0 flex size-4 -translate-x-1/2 items-center justify-center rounded-full sm:left-(--timeline-line-start-desktop)">
        <span className="bg-primary size-2 rounded-full" />
      </div>
      {/* Timeline date */}
      <div className="flex sm:justify-end">
        <span className="font-bold">{format(date, "MMM, yyyy")}</span>
      </div>

      {/* timeline card */}
      {children}
    </div>
  );
}

export default TimelineItem;
