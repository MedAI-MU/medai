export const UserRolesEnum = [
  'doctor',
  'patient',
  'secretary',
  'manager',
] as const;

export type UserRoles = (typeof UserRolesEnum)[number];

export const UserStatusEnum = ['approved', 'pending'] as const;

export type UserStatus = (typeof UserStatusEnum)[number];
