import {
  Column,
  CreateDateColumn,
  Entity,
  OneToMany,
  PrimaryGeneratedColumn,
  UpdateDateColumn,
} from 'typeorm';
import { RefreshToken } from './refresh-token.entity';

@Entity()
export class User {
  @PrimaryGeneratedColumn()
  id: number;

  @Column({ unique: true, length: 100 })
  name: string;

  @Column({ unique: true, length: 256 })
  email: string;

  @Column()
  password: string;

  @Column({ length: 20 })
  phone: string;

  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;

  @OneToMany(() => RefreshToken, (refreshToken) => refreshToken.user)
  refreshTokens: RefreshToken[];

  constructor(partial: Partial<User>) {
    Object.assign(this, partial);
  }
}
