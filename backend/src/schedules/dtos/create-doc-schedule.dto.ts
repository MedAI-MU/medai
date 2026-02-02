import {
  IsArray,
  IsDateString,
  IsInt,
  IsNotEmpty,
  ValidateNested,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';

export class CreateDocScheduleDto {
  @IsNotEmpty()
  @IsInt()
  doctorId: number;

  @IsNotEmpty()
  @IsArray({ message: 'Days must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleDayDto)
  days: CreateDocScheduleDayDto[];
}
export class CreateDocScheduleDayDto {
  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'dayDate must be a valid date string (YYYY-MM-DD)' },
  )
  date: string;

  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleSlotDto)
  slots: CreateDocScheduleSlotDto[];
}
