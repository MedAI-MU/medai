import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddAboutAndBio1783513092695 implements MigrationInterface {
  name = 'AddAboutAndBio1783513092695';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "doctor" ADD "about" text`);
    await queryRunner.query(`ALTER TABLE "user" ADD "bio" text`);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "bio"`);
    await queryRunner.query(`ALTER TABLE "doctor" DROP COLUMN "about"`);
  }
}
