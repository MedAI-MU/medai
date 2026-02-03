import { PartialType } from '@nestjs/mapped-types';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';
import { IsDateString, IsOptional } from 'class-validator';
import { Expose } from 'class-transformer';

export class UpdateDocScheduleSlotDto extends PartialType(
  CreateDocScheduleSlotDto,
) {
  @IsOptional()
  @IsDateString(
    {},
    { message: 'dayDate must be a valid date string (YYYY-MM-DD)' },
  )
  @Expose({ name: 'dayDate' })
  day?: string;
}
