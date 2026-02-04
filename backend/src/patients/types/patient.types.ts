import {
  BloodTypeEnum,
  FamilyRelationEnum,
  GenderEnum,
  MaritalStatusEnum,
} from '../enums/patients.enum';

export type Gender = (typeof GenderEnum)[number];

export type BloodType = (typeof BloodTypeEnum)[number];

export type MaritalStatus = (typeof MaritalStatusEnum)[number];

export type FamilyRelation = (typeof FamilyRelationEnum)[number];
