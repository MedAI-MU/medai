import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddStatus1783000222957 implements MigrationInterface {
  name = 'AddStatus1783000222957';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD "status" character varying NOT NULL DEFAULT 'pending'`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "status"`);
  }
}
