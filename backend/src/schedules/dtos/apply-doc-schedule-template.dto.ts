import { IsNotEmpty, IsDateString } from 'class-validator';

export class ApplyDocScheduleTemplateDto {
  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'startDate must be a valid date string (YYYY-MM-DD)' },
  )
  startDate: string;

  @IsNotEmpty()
  @IsDateString(
    {},
    { message: 'endDate must be a valid date string (YYYY-MM-DD)' },
  )
  endDate: string;
}
