import { PartialType } from '@nestjs/mapped-types';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';

export class UpdateDocScheduleSlotDto extends PartialType(
  CreateDocScheduleSlotDto,
) {}
