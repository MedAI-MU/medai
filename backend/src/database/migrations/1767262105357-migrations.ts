import { MigrationInterface, QueryRunner } from 'typeorm';

export class Migrations1767262105357 implements MigrationInterface {
  name = 'Migrations1767262105357';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "allergy" ("id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "description" text, "patientId" integer, CONSTRAINT "PK_c9cb3ece73ddfde61d2ada768e1" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "chronic_disease" ("id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "description" text, "diagnosisDate" date, "patientId" integer, CONSTRAINT "PK_bee84b15a390098f36e950147ec" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TYPE "public"."patient_gender_enum" AS ENUM('male', 'female')`,
    );
    await queryRunner.query(
      `CREATE TYPE "public"."patient_bloodtype_enum" AS ENUM('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-')`,
    );
    await queryRunner.query(
      `CREATE TYPE "public"."patient_maritalstatus_enum" AS ENUM('single', 'married', 'divorced', 'widowed')`,
    );
    await queryRunner.query(
      `CREATE TABLE "patient" ("id" SERIAL NOT NULL, "birthDate" date, "height" double precision, "weight" double precision, "gender" "public"."patient_gender_enum" NOT NULL, "bloodType" "public"."patient_bloodtype_enum", "maritalStatus" "public"."patient_maritalstatus_enum", CONSTRAINT "PK_8dfa510bb29ad31ab2139fbfb99" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" ADD CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" ADD CONSTRAINT "FK_478c6031e7a954ea93d26512314" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" DROP CONSTRAINT "FK_478c6031e7a954ea93d26512314"`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" DROP CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa"`,
    );
    await queryRunner.query(`DROP TABLE "patient"`);
    await queryRunner.query(`DROP TYPE "public"."patient_maritalstatus_enum"`);
    await queryRunner.query(`DROP TYPE "public"."patient_bloodtype_enum"`);
    await queryRunner.query(`DROP TYPE "public"."patient_gender_enum"`);
    await queryRunner.query(`DROP TABLE "chronic_disease"`);
    await queryRunner.query(`DROP TABLE "allergy"`);
  }
}
