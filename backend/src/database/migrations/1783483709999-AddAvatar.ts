import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddAvatar1783483709999 implements MigrationInterface {
  name = 'AddAvatar1783483709999';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD "avatar" character varying`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "avatar"`);
  }
}
