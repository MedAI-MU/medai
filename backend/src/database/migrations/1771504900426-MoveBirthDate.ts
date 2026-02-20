import type { MigrationInterface, QueryRunner } from 'typeorm';

export class MoveBirthDate1771504900426 implements MigrationInterface {
  name = 'MoveBirthDate1771504900426';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "patient" DROP COLUMN "birthDate"`);
    await queryRunner.query(`ALTER TABLE "user" ADD "birthDate" date`);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "birthDate"`);
    await queryRunner.query(`ALTER TABLE "patient" ADD "birthDate" date`);
  }
}
