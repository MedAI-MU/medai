import { Type } from 'class-transformer';
import {
  IsArray,
  IsOptional,
  IsString,
  Length,
  ValidateNested,
} from 'class-validator';
import { CreateDocScheduleTemplateSlotDto } from './create-doc-schedule-template-slot.dto';

export class UpdateDocScheduleTemplateDto {
  @IsOptional()
  @IsString()
  @Length(5, 100, { message: 'Name must be between 5 and 100 characters' })
  name?: string;

  @IsOptional()
  @IsArray({ message: 'Slots must be an array' })
  @ValidateNested({ each: true })
  @Type(() => CreateDocScheduleTemplateSlotDto)
  slots?: CreateDocScheduleTemplateSlotDto[];
}
