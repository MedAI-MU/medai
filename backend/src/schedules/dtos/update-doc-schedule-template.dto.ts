import { Type } from 'class-transformer';
import {
  IsArray,
  IsOptional,
  IsString,
  Length,
  ValidateNested,
} from 'class-validator';
import { CreateDocScheduleTemplateSlotDto } from './create-doc-schedule-template-slot.dto';
import { ApiProperty } from '@nestjs/swagger';

export class UpdateDocScheduleTemplateDto {
  @ApiProperty({
    description: 'Name of the schedule template',
    example: 'Monday Schedule',
  })
  @IsOptional()
  @IsString()
  @Length(5, 100, { message: 'Name must be between 5 and 100 characters' })
  name?: string;

  @ApiProperty({
    description: 'Slots of the schedule template',
    example: [
      {
        startTime: '09:00',
        endTime: '10:00',
      },
    ],
  })
  @IsOptional()
  @IsArray({ message: 'Slots must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleTemplateSlotDto)
  slots?: CreateDocScheduleTemplateSlotDto[];
}
