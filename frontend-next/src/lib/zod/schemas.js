import { z } from "zod";

const signupSchema = z.object({
  role: z
    .string()
    .regex(/^(patient|secretary|doctor)$/, "Please select a valid role."),
  name: z
    .string()
    .min(5, "Name must be between 5 and 100 characters")
    .max(100, "Name must be between 5 and 100 characters"),
  email: z
    .email("Email must be in a valid format")
    .min(5, "Email must be at least 5 characters")
    .max(256, "Email must not exceeds 256 characters"),
  phone: z
    .string()
    .min(1, "Phone is required")
    .regex(
      /^(\+2)?(010|011|012|015)\d{8}$/,
      "Phone must start with 010–015 and be 11 digits.",
    ),
  password: z
    .string()
    .min(6, "Password must be at least 6 characters")
    .max(100, "Password must not exceeds 100 characters"),
});

export { signupSchema };
