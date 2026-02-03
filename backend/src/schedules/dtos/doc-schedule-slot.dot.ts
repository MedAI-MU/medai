import type { SlotStatus } from '../types/slot-status.types';

export class DocScheduleSlotDto {
  id: number;
  startTime: string;
  endTime: string;
  status: SlotStatus;

  constructor(partial: Partial<DocScheduleSlotDto>) {
    Object.assign(this, partial);
  }
}
