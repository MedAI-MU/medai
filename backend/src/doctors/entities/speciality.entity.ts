import { Entity, Column, PrimaryGeneratedColumn, Index } from 'typeorm';

@Entity()
@Index('idx_speciality_name_trgm', { synchronize: false })
export class Speciality {
  @PrimaryGeneratedColumn()
  id: number;

  @Column()
  name: string;
}
