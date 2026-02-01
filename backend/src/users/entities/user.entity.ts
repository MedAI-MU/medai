import {
  Column,
  Entity,
  OneToMany,
  PrimaryGeneratedColumn,
  OneToOne,
} from 'typeorm';
import { RefreshToken } from './refresh-token.entity';
import { TimestampEntity } from '../../common/entities/timestamp.entity';
import type { UserRoles } from '../types/role.types';
import { Doctor } from '../../doctors/entities/doctor.entity';

@Entity()
export class User extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ length: 100 })
  name: string;

  @Column({ unique: true, length: 256 })
  email: string;

  @Column()
  password: string;

  @Column({ unique: true, length: 20 })
  phone: string;

  @OneToMany(() => RefreshToken, (refreshToken) => refreshToken.user)
  refreshTokens: RefreshToken[];

  @OneToOne(() => Doctor, (doctor) => doctor.user)
  doctor?: Doctor;

  @Column({ type: 'varchar' })
  role: UserRoles;

  constructor(partial: Partial<User>) {
    super();
    Object.assign(this, partial);
  }
}
