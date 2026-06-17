import { z } from "zod";
import { isBefore, startOfDay } from "date-fns";
import {
  formatDate,
  hasOverlappingSlots,
  isEndAfterStart,
} from "@/lib/utils/DateTimeHelpers";

// private — base fields shared by all time-range schemas
const timeBase = z.object({
  startTime: z.string().min(1, "Start time is required"),
  endTime: z.string().min(1, "End time is required"),
});

// private — applies start-before-end validation to any schema that has startTime/endTime
const timeRefine = (schema) =>
  schema.refine((data) => isEndAfterStart(data.startTime, data.endTime), {
    message: "End time must be after start time",
    path: ["endTime"],
  });

// public variants — extend with whatever extra field the form needs
export const timeValidationWithWeekDay = timeRefine(
  timeBase.extend({ weekDay: z.number().min(0).max(6) }),
);

export const timeValidationWithDayDate = timeRefine(
  timeBase.extend({
    dayDate: z
      .date({ required_error: "Please select a slot date." })
      .refine((date) => !isBefore(date, startOfDay(new Date())), {
        message: "Date must be greater than or equal today",
      }),
  }),
);

export const ScheduleTemplateSchema = z.object({
  name: z
    .string()
    .min(5, "Name must be between 5 and 100 characters")
    .max(100, "Name must be between 5 and 100 characters"),
  slots: z
    .array(timeValidationWithWeekDay)
    .min(1, "Pick at least one day")
    // Validate Overlaping times
    .superRefine((slots, ctx) => {
      // Map slots to include their original index so we know where to attach the error
      const slotsWithIndex = slots.map((slot, index) => ({
        ...slot,
        originalIndex: index,
      }));

      // Group by day to check for overlaps
      const slotsByDay = {};
      for (const slot of slotsWithIndex) {
        if (!slotsByDay[slot.weekDay]) slotsByDay[slot.weekDay] = [];
        slotsByDay[slot.weekDay].push(slot);
      }

      for (const day in slotsByDay)
        if (hasOverlappingSlots(slotsByDay[day]))
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: "Time slots cannot overlap.",
            path: [day],
          });
    }),
});

export const ScheduleSlotsSchema = z.object({
  days: z
    .array(
      z.object({
        date: z
          .date({ required_error: "Please select a slot date." })
          .refine((date) => !isBefore(date, startOfDay(new Date())), {
            message: "Date must be greater than or equal today",
          }),
        slots: z.array(timeRefine(timeBase)).min(1, "Pick at least one slot"),
      }),
    )
    .min(1, "Pick at least one day")
    .superRefine((data, ctx) => {
      // 1) Validate unique days
      const uniqueDates = new Set();

      data.forEach(({ date, slots }, dayIndex) => {
        const dateKey = formatDate(date);
        if (uniqueDates.has(dateKey)) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: "This date has already been added.",
            path: [dayIndex, "date"],
          });
        }
        uniqueDates.add(dateKey);

        // 2) Validate overlapping slots for each day
        if (hasOverlappingSlots(slots)) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: "Time slots cannot overlap.",
            path: [dayIndex],
          });
        }
      });
    }),
});
