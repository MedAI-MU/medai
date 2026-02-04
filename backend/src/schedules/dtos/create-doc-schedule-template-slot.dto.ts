import { IsNotEmpty, IsInt, IsString, Matches } from 'class-validator';
import { IsRange } from '../decorators/is-range.decorator';
import { Expose } from 'class-transformer';
import { ApiProperty } from '@nestjs/swagger';

export class CreateDocScheduleTemplateSlotDto {
  @ApiProperty({
    description: 'The day of the week for the schedule template slot',
    example: 0,
  })
  @IsNotEmpty()
  @IsInt()
  @IsRange(0, 6, {
    message: 'Week day must be between 0 (Sunday) and 6 (Saturday)',
  })
  @Expose({ name: 'weekDay' })
  day: number;

  @ApiProperty({
    description: 'The start time of the schedule template slot',
    example: '09:00',
  })
  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'start time must be in HH:MM 24-hour format',
  })
  startTime: string;

  @ApiProperty({
    description: 'The end time of the schedule template slot',
    example: '17:00',
  })
  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'end time must be in HH:MM 24-hour format',
  })
  endTime: string;
}
