import {
  IsEmail,
  IsIn,
  IsNotEmpty,
  IsString,
  Length,
  Matches,
} from 'class-validator';
import { type UserRole, UserRoles } from '../types/role.types';

export class RegisterDto {
  @Length(5, 100)
  @IsNotEmpty()
  @IsString()
  name: string;

  @Length(5, 256)
  @IsEmail({}, { message: 'email must be in valid format' })
  @IsNotEmpty()
  @IsString()
  email: string;

  @Length(6, 100)
  @IsNotEmpty()
  @IsString()
  password: string;

  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
  })
  @IsNotEmpty()
  @IsString()
  phone: string;

  @IsNotEmpty()
  @IsString()
  @IsIn([UserRoles.DOCTOR, UserRoles.PATIENT], {
    message: 'role must be either doctor or patient',
  })
  role: UserRole;
}
