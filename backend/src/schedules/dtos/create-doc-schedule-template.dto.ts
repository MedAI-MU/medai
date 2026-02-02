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

export class CreateDocScheduleTemplateDto {
  @IsString()
  @IsNotEmpty()
  @Length(5, 100, { message: 'Name must be between 5 and 100 characters' })
  name: string;

  @IsNotEmpty()
  @IsArray({ message: 'Slots must be an array' })
  @ArrayMinSize(1, { message: 'Slots must have at least one item' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleTemplateSlotDto)
  slots: CreateDocScheduleTemplateSlotDto[];
}
