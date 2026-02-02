import { IsNotEmpty, IsString, Matches } from 'class-validator';

export class CreateDocScheduleSlotDto {
  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'start time must be in HH:MM 24-hour format',
  })
  startTime: string;

  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'end time must be in HH:MM 24-hour format',
  })
  endTime: string;
}
