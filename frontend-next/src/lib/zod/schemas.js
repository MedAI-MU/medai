import { ROLES } from "@/constants/roles";
import { z } from "zod";
import { isEndAfterStart } from "../utils/DateTimeHelpers";

const signupSchema = z.object({
  role: z.enum(ROLES, "Please select a valid role."),
  name: z
    .string("Invalid name format")
    .min(5, "Name must be between 5 and 100 characters")
    .max(100, "Name must be between 5 and 100 characters"),
  email: z
    .email("Invalid email format")
    .min(5, "Email must be at least 5 characters")
    .max(256, "Email must not exceeds 256 characters"),
  phone: z
    .string("Invalid phone number format")
    .min(1, "Phone is required")
    .regex(
      /^(\+2)?(010|011|012|015)\d{8}$/,
      "Phone must start with 010–015 and be 11 digits.",
    ),
  password: z
    .string("Invalid password format")
    .min(6, "Password must be at least 6 characters")
    .max(100, "Password must not exceeds 100 characters"),
});

const loginSchema = z.object({
  email: z.email("Email must be in a valid format"),
  password: z
    .string("Invalid password format")
    .nonempty("Password is required"),
});

const ScheduleTemplateSchema = z.object({
  name: z
    .string()
    .min(5, "Name must be between 5 and 100 characters")
    .max(100, "Name must be between 5 and 100 characters"),
  slots: z
    .array(
      z
        .object({
          weekDay: z.number().min(0).max(6),
          startTime: z.string().min(1, "Start time is required"),
          endTime: z.string().min(1, "End time is required"),
        })
        // Validate each time period
        .refine((data) => isEndAfterStart(data.startTime, data.endTime), {
          message: "End time must be after start time",
          path: ["endTime"],
        }),
    )
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

      for (const day in slotsByDay) {
        const sorted = [...slotsByDay[day]].sort((a, b) =>
          a.startTime.localeCompare(b.startTime),
        );
        for (let i = 0; i < sorted.length - 1; i++) {
          // If current slot's end time is strictly greater than the next slot's start time, they overlap
          if (sorted[i].endTime > sorted[i + 1].startTime) {
            ctx.addIssue({
              code: z.ZodIssueCode.custom,
              message: "Time slots cannot overlap.",
              path: [day],
            });
          }
        }
      }
    }),
});

export { signupSchema, loginSchema, ScheduleTemplateSchema };
