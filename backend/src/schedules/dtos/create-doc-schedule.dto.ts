import {
  IsArray,
  IsDateString,
  IsNotEmpty,
  ValidateNested,
  ArrayMinSize,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';

export class CreateDocScheduleDto {
  @IsNotEmpty()
  @IsArray({ message: 'Days must be an array' })
  @ArrayMinSize(1, { message: 'Days must have at least one item' })
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
  @ArrayMinSize(1, { message: 'Slots must have at least one item' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleSlotDto)
  slots: CreateDocScheduleSlotDto[];
}
