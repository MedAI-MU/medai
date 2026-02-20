import {
  IsArray,
  IsDateString,
  IsNotEmpty,
  ValidateNested,
  ArrayMinSize,
} from 'class-validator';
import { Type } from 'class-transformer';
import { CreateDocScheduleSlotDto } from './create-doc-schedule-slot.dto';
import { ApiProperty } from '@nestjs/swagger';

export class CreateDocScheduleDto {
  @ApiProperty({
    description: 'Days of the schedule',
    example: [
      {
        date: '2023-09-25',
        slots: [
          {
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
    ],
  })
  @IsNotEmpty()
  @IsArray({ message: 'Days must be an array' })
  @ArrayMinSize(1, { message: 'Days must have at least one item' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleDayDto)
  days: CreateDocScheduleDayDto[];
}
export class CreateDocScheduleDayDto {
  @ApiProperty({
    description: 'Date of the schedule day',
    example: '2023-09-25',
  })
  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'dayDate must be a valid date string (YYYY-MM-DD)' },
  )
  date: string;

  @ApiProperty({
    description: 'Slots of the schedule day',
    example: [
      {
        startTime: '09:00',
        endTime: '10:00',
      },
    ],
  })
  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ArrayMinSize(1, { message: 'Slots must have at least one item' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleSlotDto)
  slots: CreateDocScheduleSlotDto[];
}
