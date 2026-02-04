import { PagedListDto } from '../../shared/dtos/paged-list.dto';
import { DocScheduleSlotDto } from './doc-schedule-slot.dot';

export class DocScheduleDto {
  doctorId: number;
  name: string;
  speciality: string;
  days: PagedListDto<DocScheduleDayDto>;

  constructor(partial: Partial<DocScheduleDto>) {
    Object.assign(this, partial);
  }
}

export class DocScheduleDayDto {
  day: string;
  slots: DocScheduleSlotDto[];

  constructor(partial: Partial<DocScheduleDayDto>) {
    Object.assign(this, partial);
  }
}
