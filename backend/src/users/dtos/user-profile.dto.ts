import { ApiProperty } from '@nestjs/swagger';
import type { Gender } from '../../shared/types/gender.type';

export class UserProfileDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  name: string;

  @ApiProperty()
  email: string;

  @ApiProperty()
  phone: string;

  @ApiProperty({ nullable: true })
  birthDate?: Date | null;

  @ApiProperty({ nullable: true, enum: ['male', 'female'] })
  gender?: Gender | null;

  @ApiProperty()
  role: string;

  @ApiProperty()
  status: string;

  @ApiProperty({ nullable: true })
  avatar?: string | null;

  @ApiProperty()
  createdAt: Date;

  @ApiProperty()
  updatedAt: Date;

  constructor(user: {
    id: number;
    name: string;
    email: string;
    phone: string;
    birthDate?: Date | null;
    gender?: Gender | null;
    role: string;
    status: string;
    avatar?: string | null;
    createdAt: Date;
    updatedAt: Date;
  }) {
    this.id = user.id;
    this.name = user.name;
    this.email = user.email;
    this.phone = user.phone;
    this.birthDate = user.birthDate ?? null;
    this.gender = user.gender ?? null;
    this.role = user.role;
    this.status = user.status;
    this.avatar = user.avatar ?? null;
    this.createdAt = user.createdAt;
    this.updatedAt = user.updatedAt;
  }
}
