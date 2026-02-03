export const UserRoles = {
  DOCTOR: 'doctor',
  PATIENT: 'patient',
  SECRETARY: 'secretary',
} as const;

export type UserRole = (typeof UserRoles)[keyof typeof UserRoles];
