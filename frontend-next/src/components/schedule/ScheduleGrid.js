"use client";

import Badge from "@/components/ui/Badge";

/**
 * ScheduleGrid - Compound Component
 * Assemble a gorgeous, interactive weekly schedule board with maximum flexibility.
 */
function ScheduleGrid({ children }) {
  return (
    <div className="scrollbar-thumb-surface-overlay bg-surface border-border mt-4 scrollbar-thin overflow-hidden overflow-x-auto rounded-lg border shadow-sm transition-all duration-300">
      <div className="flex min-w-[1000px] flex-col">{children}</div>
    </div>
  );
}

// ── HEADER CONTAINER ──
ScheduleGrid.Header = function Header({ children }) {
  return (
    <div className="border-border bg-surface/50 grid grid-cols-7 border-b backdrop-blur-xs">
      {children}
    </div>
  );
};

// ── HEADER CELL ──
ScheduleGrid.HeaderCell = function HeaderCell({
  dayName = "",
  dayNumber = "",
  isToday = false,
  className = "",
}) {
  return (
    <div
      className={`border-border flex flex-col items-center justify-center border-r p-4 text-center select-none last:border-r-0 ${className}`}
    >
      <span
        className={`mb-1.5 block text-xs font-bold tracking-wider uppercase ${
          isToday ? "text-primary" : "text-text-muted"
        }`}
      >
        {dayName}
      </span>
      {isToday ? (
        <span className="bg-primary relative inline-flex h-8 w-8 items-center justify-center rounded-full text-sm font-semibold text-white shadow-sm">
          {dayNumber}
          <span className="bg-primary/20 absolute top-1/2 left-1/2 -z-10 size-7 -translate-1/2 animate-ping rounded-full" />
        </span>
      ) : (
        <span className="text-text-base text-lg font-bold">{dayNumber}</span>
      )}
    </div>
  );
};

// ── BODY CONTAINER ──
ScheduleGrid.Body = function Body({ children }) {
  return (
    <div className="bg-surface divide-border grid min-h-[600px] grid-cols-7 divide-x">
      {children}
    </div>
  );
};

// ── COLUMN FOR A SINGLE DAY ──
ScheduleGrid.Column = function Column({
  children,
  isToday = false,
  className = "",
}) {
  return (
    <div
      className={`flex flex-1 flex-col space-y-3 p-3 transition-colors duration-200 ${
        isToday ? "bg-primary/5" : "hover:bg-surface-bg/10 bg-transparent"
      } ${className}`}
    >
      {children}
    </div>
  );
};

// ── SLOT COMPONENT (E.G. AVAILABLE, BOOKED, COMPLETED, CANCELED) ──
const STATUS_STYLES = {
  available: {
    border: "border-l-success",
    label: "available",
    badge: "success",
  },
  booked: {
    border: "border-l-primary",
    label: "booked",
    badge: "blue",
  },
  canceled: {
    border: "border-l-warning",
    label: "canceled",
    badge: "warning",
  },
  completed: {
    border: "border-l-text-subtle",
    label: "completed",
    badge: "subtle",
  },
};

ScheduleGrid.Slot = function Slot({
  startTime = "",
  endTime = "",
  status = "available", // "available" | "booked" | "canceled" | "completed"
  onClick,
  className = "",
}) {
  const styles = STATUS_STYLES[status] || STATUS_STYLES.available;

  return (
    <div
      onClick={onClick}
      className={`bg-surface border-border border border-l-4 ${
        styles.border
      } flex cursor-pointer flex-col gap-2 rounded-sm p-3 shadow-xs transition-all duration-200 select-none hover:shadow-md active:scale-[0.98] ${
        onClick ? "cursor-pointer" : "pointer-events-none cursor-default"
      } ${className}`}
    >
      <div className="flex items-center justify-between gap-1">
        <span className="text-text-base text-sm font-semibold transition-colors">
          {startTime} - {endTime}
        </span>
      </div>

      <Badge
        text={styles.label}
        color={styles.badge}
        className="w-fit tracking-wide"
      />
    </div>
  );
};

// ── EMPTY STATE FOR A DAY ──
ScheduleGrid.EmptyState = function EmptyState({
  message = "No slots",
  className = "",
}) {
  return (
    <div
      className={`border-border text-text-subtle bg-surface-bg/20 hover:border-text-subtle/30 flex h-32 items-center justify-center rounded-lg border-2 border-dashed p-4 text-center text-sm font-medium transition-all duration-200 select-none ${className}`}
    >
      {message}
    </div>
  );
};

export default ScheduleGrid;
