import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import { Patient } from '../entities/patient.entity';
import {
  BloodTypeEnum,
  MaritalStatusEnum,
  FamilyRelationEnum,
} from '../enums/patients.enum';
import type {
  BloodType,
  MaritalStatus,
  FamilyRelation,
} from '../types/patient.types';
import { Allergy } from '../entities/allergy.entity';
import { ChronicDisease } from '../entities/chronic_disease.entity';
import { Surgery } from '../entities/surgery.entity';
import { FamilyHistory } from '../entities/family_history.entity';
import { EmergencyContact } from '../entities/emergency_contact.entity';

export class AllergyResponseDto {
  @ApiProperty() id: number;
  @ApiProperty() name: string;
  @ApiPropertyOptional() description?: string;
  constructor(partial: Partial<Allergy>) {
    Object.assign(this, partial);
  }
}

export class ChronicDiseaseResponseDto {
  @ApiProperty() id: number;
  @ApiProperty() name: string;
  @ApiPropertyOptional() diagnosisDate?: Date;
  @ApiPropertyOptional() description?: string;
  constructor(partial: Partial<ChronicDisease>) {
    Object.assign(this, partial);
  }
}

export class SurgeryResponseDto {
  @ApiProperty() id: number;
  @ApiProperty() name: string;
  @ApiProperty() date: Date;
  @ApiPropertyOptional() description?: string;
  constructor(partial: Partial<Surgery>) {
    Object.assign(this, partial);
  }
}

export class FamilyHistoryResponseDto {
  @ApiProperty() id: number;
  @ApiProperty() condition: string;
  @ApiProperty({ enum: FamilyRelationEnum }) relation: FamilyRelation;
  @ApiPropertyOptional() notes?: string;
  constructor(partial: Partial<FamilyHistory>) {
    Object.assign(this, partial);
  }
}

export class EmergencyContactResponseDto {
  @ApiProperty() id: number;
  @ApiProperty() name: string;
  @ApiProperty({ enum: FamilyRelationEnum }) relation: FamilyRelation;
  @ApiProperty() phoneNumber: string;
  @ApiProperty() email: string;
  @ApiProperty() address: string;
  @ApiPropertyOptional() notes?: string;
  constructor(partial: Partial<EmergencyContact>) {
    Object.assign(this, partial);
  }
}

export class PatientResponseDto {
  @ApiProperty()
  userId: number;

  @ApiPropertyOptional()
  name?: string;

  @ApiPropertyOptional()
  height?: number;

  @ApiPropertyOptional()
  weight?: number;

  @ApiPropertyOptional({ enum: BloodTypeEnum })
  bloodType?: BloodType;

  @ApiPropertyOptional({ enum: MaritalStatusEnum })
  maritalStatus?: MaritalStatus;

  @ApiPropertyOptional({ type: [AllergyResponseDto] })
  allergies?: AllergyResponseDto[];

  @ApiPropertyOptional({ type: [ChronicDiseaseResponseDto] })
  chronicDiseases?: ChronicDiseaseResponseDto[];

  @ApiPropertyOptional({ type: [SurgeryResponseDto] })
  surgeries?: SurgeryResponseDto[];

  @ApiPropertyOptional({ type: [FamilyHistoryResponseDto] })
  familyHistories?: FamilyHistoryResponseDto[];

  @ApiPropertyOptional({ type: [EmergencyContactResponseDto] })
  emergencyContacts?: EmergencyContactResponseDto[];

  constructor(partial: Partial<Patient>) {
    this.userId = partial.userId as number;
    this.name = partial.user?.name;
    this.height = partial.height;
    this.weight = partial.weight;
    this.bloodType = partial.bloodType;
    this.maritalStatus = partial.maritalStatus;

    if (partial.allergies) {
      this.allergies = partial.allergies.map((a) => new AllergyResponseDto(a));
    }
    if (partial.chronicDiseases) {
      this.chronicDiseases = partial.chronicDiseases.map(
        (c) => new ChronicDiseaseResponseDto(c),
      );
    }
    if (partial.surgeries) {
      this.surgeries = partial.surgeries.map((s) => new SurgeryResponseDto(s));
    }
    if (partial.familyHistories) {
      this.familyHistories = partial.familyHistories.map(
        (f) => new FamilyHistoryResponseDto(f),
      );
    }
    if (partial.emergencyContacts) {
      this.emergencyContacts = partial.emergencyContacts.map(
        (e) => new EmergencyContactResponseDto(e),
      );
    }
  }
}
