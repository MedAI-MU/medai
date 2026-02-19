import { PartialType } from '@nestjs/mapped-types';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';
import { IsDateString, IsOptional } from 'class-validator';
import { Expose } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

export class UpdateDocScheduleSlotDto extends PartialType(
  CreateDocScheduleSlotDto,
) {
  @ApiProperty({
    description: 'The date of the schedule slot',
    example: '2023-09-25',
    required: false,
  })
  @IsOptional()
  @IsDateString(
    {},
    { message: 'dayDate must be a valid date string (YYYY-MM-DD)' },
  )
  @Expose({ name: 'dayDate' })
  day?: string;
}
