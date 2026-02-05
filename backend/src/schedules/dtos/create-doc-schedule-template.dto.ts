import { Type } from 'class-transformer';
import {
  IsArray,
  IsNotEmpty,
  IsString,
  Length,
  ValidateNested,
  ArrayMinSize,
} from 'class-validator';
import { CreateDocScheduleTemplateSlotDto } from './create-doc-schedule-template-slot.dto';
import { ApiProperty } from '@nestjs/swagger';

export class CreateDocScheduleTemplateDto {
  @ApiProperty({
    description: 'The name of the schedule template',
    example: 'October Schedule',
  })
  @IsString()
  @IsNotEmpty()
  @Length(5, 100, { message: 'Name must be between 5 and 100 characters' })
  name: string;

  @ApiProperty({
    description: 'The slots of the schedule template',
    example: [
      {
        day: 1,
        startTime: '09:00',
        endTime: '17:00',
      },
    ],
  })
  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ArrayMinSize(1, { message: 'Slots must have at least one item' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleTemplateSlotDto)
  slots: CreateDocScheduleTemplateSlotDto[];
}
