import { Column, Entity, ManyToOne, PrimaryGeneratedColumn } from 'typeorm';
import { TimestampEntity } from '../../shared/entities/timestamp.entity';
import { Scan } from './scan.entity';

@Entity()
export class ScanImage extends TimestampEntity {
  @PrimaryGeneratedColumn()
  id: number;

  @ManyToOne(() => Scan, { onDelete: 'CASCADE' })
  scan: Scan;

  @Column()
  scanId: number;

  @Column()
  path: string;
}
