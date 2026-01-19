import { Type } from 'class-transformer';
import {
  IsArray,
  IsInt,
  IsNotEmpty,
  IsString,
  Length,
  Matches,
  ValidateNested,
} from 'class-validator';
import { IsRange } from '../decorators/is-range.decorator';

export class CreateDocScheduleTemplateDto {
  @IsString()
  @IsNotEmpty()
  @Length(5, 100, { message: 'Name must be between 5 and 100 characters' })
  name: string;

  @IsNotEmpty()
  @IsInt()
  doctorId: number;

  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateTemplateScheduleSlotDto)
  slots: CreateTemplateScheduleSlotDto[];
}

export class CreateTemplateScheduleSlotDto {
  @IsNotEmpty()
  @IsInt()
  @IsRange(0, 6, {
    message: 'Week day must be between 0 (Saturday) and 6 (Friday)',
  })
  weekDay: number;

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
