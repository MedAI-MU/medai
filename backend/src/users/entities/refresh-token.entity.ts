import {
  Column,
  Entity,
  Index,
  ManyToOne,
  PrimaryGeneratedColumn,
} from 'typeorm';
import { User } from './user.entity';

@Entity()
export class RefreshToken {
  @PrimaryGeneratedColumn()
  id: number;
  @Column()
  @Index()
  hashedRefreshToken: string;
  @Column()
  expiresAt: Date;
  @ManyToOne(() => User, (user) => user.refreshTokens, { onDelete: 'CASCADE' })
  user: User;

  constructor(partial: Partial<RefreshToken>) {
    Object.assign(this, partial);
  }
}
