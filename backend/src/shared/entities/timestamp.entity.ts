import { CreateDateColumn, UpdateDateColumn, BaseEntity } from 'typeorm';

export abstract class TimestampEntity extends BaseEntity {
  @CreateDateColumn()
  createdAt: Date;

  @UpdateDateColumn()
  updatedAt: Date;
}
