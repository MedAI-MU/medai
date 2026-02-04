import { ApiProperty } from '@nestjs/swagger';
import { IsNotEmpty, IsString, Matches } from 'class-validator';

export class CreateDocScheduleSlotDto {
  @ApiProperty({
    description: 'The start time of the schedule slot',
    example: '09:00',
  })
  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'start time must be in HH:MM 24-hour format',
  })
  startTime: string;

  @ApiProperty({
    description: 'The end time of the schedule slot',
    example: '10:00',
  })
  @IsNotEmpty()
  @IsString()
  @Matches(/^(?:[01][0-9]|2[0-3]):[0-5][0-9]$/, {
    message: 'end time must be in HH:MM 24-hour format',
  })
  endTime: string;
}
