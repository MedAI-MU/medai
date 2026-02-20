import { PartialType } from '@nestjs/swagger';
import { ChronicDiseaseDto } from './chronic_disease.dto';

export class UpdateChronicDiseaseDto extends PartialType(ChronicDiseaseDto) {}
