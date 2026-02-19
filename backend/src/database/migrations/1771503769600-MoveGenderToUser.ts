import type { MigrationInterface, QueryRunner } from 'typeorm';

export class MoveGenderToUser1771503769600 implements MigrationInterface {
  name = 'MoveGenderToUser1771503769600';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`ALTER TABLE "patient" DROP COLUMN "gender"`);
    await queryRunner.query(`DROP TYPE "public"."patient_gender_enum"`);
    await queryRunner.query(
      `CREATE TYPE "public"."user_gender_enum" AS ENUM('male', 'female')`,
    );
    await queryRunner.query(
      `ALTER TABLE "user" ADD "gender" "public"."user_gender_enum"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ALTER COLUMN "isPrimary" SET DEFAULT false`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ALTER COLUMN "yearsOfExperience" SET DEFAULT '0'`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ALTER COLUMN "yearsOfExperience" DROP DEFAULT`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ALTER COLUMN "isPrimary" DROP DEFAULT`,
    );
    await queryRunner.query(`ALTER TABLE "user" DROP COLUMN "gender"`);
    await queryRunner.query(`DROP TYPE "public"."user_gender_enum"`);
    await queryRunner.query(
      `CREATE TYPE "public"."patient_gender_enum" AS ENUM('male', 'female')`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" ADD "gender" "public"."patient_gender_enum" NOT NULL`,
    );
  }
}
