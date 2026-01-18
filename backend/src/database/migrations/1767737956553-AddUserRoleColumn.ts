import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddUserRoleColumn1767737956553 implements MigrationInterface {
  name = 'AddUserRoleColumn1767737956553';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD "role" character varying NOT NULL`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "role"`);
  }
}
