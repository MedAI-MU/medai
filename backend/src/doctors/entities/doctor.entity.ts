import { Entity, PrimaryColumn, Column, OneToOne, JoinColumn } from 'typeorm';
import { User } from '../../users/entities/user.entity';

@Entity()
export class Doctor {
  @PrimaryColumn()
  userId: number;

  @OneToOne(() => User, (user) => user.doctor)
  @JoinColumn({ name: 'userId' })
  user: User;

  @Column()
  specialty: string;

  constructor(partial: Partial<Doctor>) {
    Object.assign(this, partial);
  }
}
