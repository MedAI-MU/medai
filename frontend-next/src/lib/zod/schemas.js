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
        .refine((data) => isEndAfterStart(data.startTime, data.endTime), {
          message: "End time must be after start time",
          path: ["endTime"],
        }),
    )
    .min(1, "Pick at least one day"),
});

export { signupSchema, loginSchema, ScheduleTemplateSchema };
