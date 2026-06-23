"use client";

import { format, parseISO } from "date-fns";
import { cn } from "@/lib/utils";

function DayCard({ date, hasSlots, isSelected, isCompleted, onClick }) {
  const parsed = parseISO(date);
  const dayName = format(parsed, "EEE"); // "Tue"
  const dayNum = format(parsed, "dd"); // "19"
  const month = format(parsed, "MMM"); // "May"

  // 1) Non clickable day card
  if (!onClick)
    return (
      <div
        className={cn(
          "border-border bg-surface-overlay text-text-base z-10 flex h-28 w-24 shrink-0 flex-col items-center justify-center rounded-xl border-2",
          isCompleted && "text-text-subtle opacity-70",
        )}
      >
        <span className="mb-0.5 text-[11px] font-semibold tracking-wider uppercase opacity-80">
          {dayName}
        </span>
        <span
          className={cn(
            "text-text-base text-3xl leading-none font-bold",
            isCompleted && "text-text-subtle",
          )}
        >
          {dayNum}
        </span>
        <span className="mt-0.5 text-[11px] opacity-80">{month}</span>
      </div>
    );

  // 2) Clickable day card
  const isDisabled = !hasSlots;

  return (
    <button
      onClick={!isDisabled ? onClick : undefined}
      disabled={isDisabled}
      className={cn(
        "z-10 flex h-28 w-24 shrink-0 flex-col items-center justify-center rounded-xl border-2 transition-all duration-200",
        isSelected && "border-primary bg-primary text-white shadow-md",
        !isSelected &&
          !isDisabled &&
          "border-border bg-surface text-text-base hover:border-primary cursor-pointer hover:-translate-y-1",
        isDisabled &&
          "border-border bg-surface-overlay text-text-subtle cursor-not-allowed opacity-60",
      )}
    >
      <span className="mb-0.5 text-[11px] font-semibold tracking-wider uppercase opacity-80">
        {dayName}
      </span>
      <span
        className={cn(
          "text-3xl leading-none font-bold",
          isSelected && "text-white",
          !isSelected && !isDisabled && "text-text-base",
          isDisabled && "text-text-subtle",
        )}
      >
        {dayNum}
      </span>
      <span className="mt-0.5 text-[11px] opacity-80">{month}</span>

      {/* Availability dot */}
      {!isDisabled && (
        <span
          className={cn(
            "mt-2 h-1.5 w-1.5 rounded-full",
            isSelected ? "bg-white/80" : "bg-success",
          )}
        />
      )}
    </button>
  );
}

export default DayCard;
