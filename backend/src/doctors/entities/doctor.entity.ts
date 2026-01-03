import {
  Entity,
  PrimaryGeneratedColumn,
  Column,
  OneToOne,
  JoinColumn,
} from 'typeorm';
import { User } from '../../users/entities/user.entity';

@Entity('doctor')
export class Doctor {
  @PrimaryGeneratedColumn()
  id: number;

  @OneToOne(() => User, (user) => user.doctor)
  @JoinColumn()
  user: User;

  @Column()
  specialty: string;

  constructor(partial: Partial<Doctor>) {
    Object.assign(this, partial);
  }
}
