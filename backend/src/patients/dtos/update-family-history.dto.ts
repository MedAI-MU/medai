import { PartialType } from '@nestjs/swagger';
import { FamilyHistoryDto } from './family_history.dto';

export class UpdateFamilyHistoryDto extends PartialType(FamilyHistoryDto) {}
