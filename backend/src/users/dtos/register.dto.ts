import { IsEmail, Length, Matches } from 'class-validator';

export class RegisterDto {
  @Length(5, 100)
  name: string;

  @Length(5, 256)
  @IsEmail({}, { message: 'email must be in valid format' })
  email: string;

  @Length(6, 100)
  password: string;

  @Matches(/^(010|011|012|015)\d{8}$/, {
    message:
      'Phone must start with 010, 011, 012 or 015 and be followed by 8 digits',
  })
  phone: string;
}
