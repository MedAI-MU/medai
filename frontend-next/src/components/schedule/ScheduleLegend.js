const statuses = [
  {
    label: "Available",
    color: "bg-success",
  },
  {
    label: "Booked",
    color: "bg-primary",
  },
  {
    label: "Canceled",
    color: "bg-warning",
  },
  {
    label: "Completed",
    color: "bg-text-subtle",
  },
];

export default function ScheduleLegend() {
  return (
    <div className="flex gap-6 max-md:hidden">
      {statuses.map((status) => (
        <div key={status.label} className="flex items-center gap-2">
          <span className={`h-2 w-2 rounded-full ${status.color}`} />

          <span className="text-text-muted text-sm">{status.label}</span>
        </div>
      ))}
    </div>
  );
}
