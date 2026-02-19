import { GenderEnum } from '../enums/gender.enum';

export type Gender = (typeof GenderEnum)[number];
