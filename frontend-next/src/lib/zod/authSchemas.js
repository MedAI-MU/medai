import { ROLES } from "@/constants/roles";
import { z } from "zod";

export const signupSchema = z.object({
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
      /^(010|011|012|015)\d{8}$/,
      "Phone must start with 010–011–012–015 and be 11 digits.",
    ),
  password: z
    .string("Invalid password format")
    .min(6, "Password must be at least 6 characters")
    .max(100, "Password must not exceeds 100 characters"),
});

export const loginSchema = z.object({
  email: z.email("Email must be in a valid format"),
  password: z
    .string("Invalid password format")
    .nonempty("Password is required"),
});

export const forgotPasswordSchema = z.object({
  email: z.email("Please enter a valid email address"),
});

export const resetPasswordSchema = z
  .object({
    password: z
      .string("Invalid password format")
      .min(6, "Password must be at least 6 characters")
      .max(100, "Password must not exceed 100 characters"),
    confirmPassword: z
      .string("Invalid password format")
      .min(1, "Please confirm your password"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Passwords do not match",
    path: ["confirmPassword"],
  });

export const profileSchema = z.object({
  name: z.string().min(5, "Name must be at least 5 characters").max(100),
  phone: z
    .string()
    .regex(/^(010|011|012|015)\d{8}$/, "Invalid Egyptian phone number"),
  birthDate: z.date().optional().nullable(),
  gender: z.enum(["male", "female", ""]).optional(),
  bio: z.string().optional().or(z.literal("")),
});
