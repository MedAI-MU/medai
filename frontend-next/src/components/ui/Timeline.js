function Timeline({ items, render }) {
  return (
    <div
      style={{
        "--timeline-container-gap": "40px",
        "--timeline-item-gap-desktop":
          "calc(var(--timeline-container-gap) * 0.75)",
        "--timeline-item-gap-mobile":
          "calc(var(--timeline-container-gap) * 0.25)",
        "--timeline-item-date-width": "120px",
        "--timeline-pd-start": "24px",
        "--timeline-line-start-desktop":
          "calc(var(--timeline-item-date-width) + var(--timeline-pd-start) + var(--timeline-item-gap-desktop) * 0.5)",
      }}
      className="relative flex flex-col gap-(--timeline-container-gap)"
    >
      <div className="absolute inset-y-0 left-0 w-px -translate-x-1/2 bg-slate-200 sm:left-(--timeline-line-start-desktop)" />
      {items && items.map(render)}
    </div>
  );
}

export default Timeline;
