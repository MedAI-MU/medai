import {
  BloodTypeEnum,
  FamilyRelationEnum,
  MaritalStatusEnum,
} from '../enums/patients.enum';

export type BloodType = (typeof BloodTypeEnum)[number];

export type MaritalStatus = (typeof MaritalStatusEnum)[number];

export type FamilyRelation = (typeof FamilyRelationEnum)[number];

export type RelationType =
  | 'allergies'
  | 'chronicDiseases'
  | 'surgeries'
  | 'familyHistories'
  | 'emergencyContacts';

export type DtoType =
  | 'AllergyDto'
  | 'ChronicDiseaseDto'
  | 'SurgeryDto'
  | 'FamilyHistoryDto'
  | 'EmergencyContactDto';
