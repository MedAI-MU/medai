import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddAppointmentReview1746461100000 implements MigrationInterface {
  name = 'AddAppointmentReview1746461100000';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "appointment" ADD "rating" integer`);
    await queryRunner.query(`ALTER TABLE "appointment" ADD "review" text`);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "appointment" DROP COLUMN "review"`);
    await queryRunner.query(`ALTER TABLE "appointment" DROP COLUMN "rating"`);
  }
}
