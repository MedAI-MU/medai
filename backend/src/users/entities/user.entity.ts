import { Column, Entity, OneToMany, PrimaryGeneratedColumn } from 'typeorm';
import { RefreshToken } from './refresh-token.entity';

import { TimestampEntity } from '../../common/entities/timestamp.entity';

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

  constructor(partial: Partial<User>) {
    super();
    Object.assign(this, partial);
  }
}
