import { Type } from 'class-transformer';
import {
  IsArray,
  IsInt,
  IsNotEmpty,
  IsString,
  Length,
  ValidateNested,
} from 'class-validator';
import { CreateDocScheduleTemplateSlotDto } from './create-doc-schedule-template-slot.dto';

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
  @Type(() => CreateDocScheduleTemplateSlotDto)
  slots: CreateDocScheduleTemplateSlotDto[];
}
