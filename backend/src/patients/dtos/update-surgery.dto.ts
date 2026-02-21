import { PartialType } from '@nestjs/swagger';
import { SurgeryDto } from './surgery.dto';

export class UpdateSurgeryDto extends PartialType(SurgeryDto) {}
