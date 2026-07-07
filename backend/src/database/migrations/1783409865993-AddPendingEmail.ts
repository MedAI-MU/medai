import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddPendingEmail1783409865993 implements MigrationInterface {
  name = 'AddPendingEmail1783409865993';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD "pendingEmail" character varying`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "pendingEmail"`);
  }
}
