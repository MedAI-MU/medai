import { IsNotEmpty, IsInt, IsString, Matches } from 'class-validator';
import { IsRange } from '../decorators/is-range.decorator';
import { Expose } from 'class-transformer';

export class CreateDocScheduleTemplateSlotDto {
  @IsNotEmpty()
  @IsInt()
  @IsRange(0, 6, {
    message: 'Week day must be between 0 (Saturday) and 6 (Friday)',
  })
  @Expose({ name: 'weekDay' })
  day: number;

  @IsNotEmpty()
  @IsString()
  @Matches(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'start time must be in HH:MM 24-hour format',
  })
  startTime: string;

  @IsNotEmpty()
  @IsString()
  @Matches(/^([01]?[0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'end time must be in HH:MM 24-hour format',
  })
  endTime: string;
}
