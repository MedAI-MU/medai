import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsDateString } from 'class-validator';

export class ApplyDocScheduleTemplateDto {
  @ApiProperty({
    description: 'The start date of the schedule template',
    example: '2023-01-01',
  })
  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'startDate must be a valid date string (YYYY-MM-DD)' },
  )
  startDate: string;

  @ApiProperty({
    description: 'The end date of the schedule template',
    example: '2023-01-31',
  })
  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'endDate must be a valid date string (YYYY-MM-DD)' },
  )
  endDate: string;
}
