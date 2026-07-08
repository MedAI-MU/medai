import { ApiProperty } from '@nestjs/swagger';

export class UserResponseDto {
  @ApiProperty()
  id: number;

  @ApiProperty()
  name: string;

  @ApiProperty({ nullable: true, enum: ['male', 'female'] })
  gender?: string | null;

  @ApiProperty()
  role: string;

  @ApiProperty()
  status: string;

  @ApiProperty({ nullable: true })
  avatar?: string | null;

  @ApiProperty()
  createdAt: Date;

  constructor(user: {
    id: number;
    name: string;
    gender?: string | null;
    role: string;
    status: string;
    avatar?: string | null;
    createdAt: Date;
  }) {
    this.id = user.id;
    this.name = user.name;
    this.gender = user.gender ?? null;
    this.role = user.role;
    this.status = user.status;
    this.avatar = user.avatar ?? null;
    this.createdAt = user.createdAt;
  }
}
