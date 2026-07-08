"use client";

import { CalendarX } from "lucide-react";
import { format } from "date-fns";
import SlotButton from "./SlotButton";
import { parseDate } from "@/lib/utils/DateTimeHelpers";

function SlotsPanel({ selectedDay, slots }) {
  const formattedDay = selectedDay
    ? format(parseDate(selectedDay), "EEEE, MMM d")
    : null;

  return (
    <div className="bg-surface border-border rounded-xl border p-5 shadow-sm">
      {/* Header */}
      <div className="mb-5 flex items-center justify-between">
        <h4 className="text-text-base font-semibold">Available Time Slots</h4>
        {formattedDay && (
          <span className="border-primary/20 bg-primary/10 text-primary rounded-full border px-3 py-1 text-xs font-medium">
            {formattedDay}
          </span>
        )}
      </div>

      {/* Slots grid or empty state */}
      {slots?.length > 0 ? (
        <div className="flex flex-wrap gap-3">
          {slots.map((slot) => (
            <SlotButton key={slot.id} slot={slot} day={formattedDay} />
          ))}
        </div>
      ) : (
        <div className="flex flex-col items-center justify-center gap-3 py-10">
          <CalendarX size={36} className="text-text-subtle" />
          <p className="text-text-muted text-sm">
            No slots available for this day.
          </p>
        </div>
      )}
    </div>
  );
}

export default SlotsPanel;
