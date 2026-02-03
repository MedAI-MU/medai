export const SlotStatus = {
  AVAILABLE: 'available',
  BOOKED: 'booked',
  CANCELLED: 'cancelled',
  COMPLETED: 'completed',
} as const;

export type SlotStatus = (typeof SlotStatus)[keyof typeof SlotStatus];
