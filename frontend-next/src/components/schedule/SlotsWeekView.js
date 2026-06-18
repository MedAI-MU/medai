"use client";

import {
  eachDayOfInterval,
  endOfWeek,
  format,
  isToday,
  startOfWeek,
} from "date-fns";
import {
  formatDate,
  formatTime12h,
  parseDate,
  stripSeconds,
} from "@/lib/utils/DateTimeHelpers";

import { Calendar } from "lucide-react";
import EmptyState from "@/components/ui/EmptyState";
import ScheduleGrid from "./ScheduleGrid";
import FormDialog from "../ui/FormDialog";
import EditDeleteSlotForm from "./EditDeleteSlotForm";

export default function SlotsWeekView({ slotsData = [], startDate }) {
  // Safely parse YYYY-MM-DD string into a local Date object to avoid timezone shifts
  const baseDate = parseDate(startDate);

  const weekStart = startOfWeek(baseDate);
  const weekEnd = endOfWeek(baseDate);
  const daysInWeek = eachDayOfInterval({ start: weekStart, end: weekEnd });

  // Map slots by their date string key
  const slotsByDay = {};
  slotsData.forEach((dayData) => {
    slotsByDay[dayData.day] = dayData.slots || [];
  });

  const hasAnySlots = slotsData.some(
    (dayData) => dayData.slots && dayData.slots.length > 0,
  );

  if (!hasAnySlots) {
    return (
      <EmptyState
        title="No slots scheduled"
        description="There are no availability slots configured for this week. Use the 'Add slots' button to schedule new time slots or apply a template."
        icon={<Calendar size={40} className="text-primary" />}
      />
    );
  }

  return (
    <ScheduleGrid>
      <ScheduleGrid.Header>
        {daysInWeek.map((day) => {
          const dayKey = formatDate(day);
          return (
            <ScheduleGrid.HeaderCell
              key={dayKey}
              dayName={format(day, "EEE")}
              dayNumber={format(day, "d")}
              isToday={isToday(day)}
            />
          );
        })}
      </ScheduleGrid.Header>

      <ScheduleGrid.Body>
        {daysInWeek.map((day) => {
          const dayKey = formatDate(day);
          const slots = slotsByDay[dayKey] || [];

          return (
            <ScheduleGrid.Column key={dayKey} isToday={isToday(day)}>
              {slots.length === 0 ? (
                <ScheduleGrid.EmptyState message="No slots" />
              ) : (
                slots.map((slot) => (
                  <FormDialog
                    key={slot.id}
                    title="Edit Slot"
                    description="Update the slot's date, start time, and end time. Save your changes when you're done."
                    form={<EditDeleteSlotForm slotToEdit={{ ...slot, day }} />}
                  >
                    <ScheduleGrid.Slot
                      startTime={formatTime12h(stripSeconds(slot.startTime))}
                      endTime={formatTime12h(stripSeconds(slot.endTime))}
                      status={slot.status}
                    />
                  </FormDialog>
                ))
              )}
            </ScheduleGrid.Column>
          );
        })}
      </ScheduleGrid.Body>
    </ScheduleGrid>
  );
}
