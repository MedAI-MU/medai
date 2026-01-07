import type { MigrationInterface, QueryRunner } from 'typeorm';

export class InitialPatient1767779311496 implements MigrationInterface {
  name = 'InitialPatient1767779311496';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "allergy" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "description" text, "patientId" integer, CONSTRAINT "PK_c9cb3ece73ddfde61d2ada768e1" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "chronic_disease" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "description" text, "diagnosisDate" date, "patientId" integer, CONSTRAINT "PK_bee84b15a390098f36e950147ec" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "surgery" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "date" date NOT NULL, "description" text, "patientId" integer, CONSTRAINT "PK_2e963fc0e35d07a36e15f331754" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TYPE "public"."emergency_contact_relation_enum" AS ENUM('father', 'mother', 'sibling', 'child', 'spouse', 'other')`,
    );
    await queryRunner.query(
      `CREATE TABLE "emergency_contact" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "relation" "public"."emergency_contact_relation_enum" NOT NULL, "phoneNumber" character varying(15) NOT NULL, "email" character varying(100) NOT NULL, "address" text NOT NULL, "notes" text, "patientId" integer, CONSTRAINT "PK_922933ddef34a7e1ed99ae692ce" PRIMARY KEY ("id"))`,
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
      `CREATE TABLE "patient" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "birthDate" date, "height" double precision, "weight" double precision, "gender" "public"."patient_gender_enum" NOT NULL, "bloodType" "public"."patient_bloodtype_enum", "maritalStatus" "public"."patient_maritalstatus_enum", CONSTRAINT "PK_8dfa510bb29ad31ab2139fbfb99" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TYPE "public"."family_history_relation_enum" AS ENUM('father', 'mother', 'sibling', 'child', 'spouse', 'other')`,
    );
    await queryRunner.query(
      `CREATE TABLE "family_history" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "relation" "public"."family_history_relation_enum" NOT NULL, "condition" character varying(100) NOT NULL, "notes" text, "patientId" integer, CONSTRAINT "PK_cbd60bda074eb1c7c17fea20390" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" ADD CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" ADD CONSTRAINT "FK_478c6031e7a954ea93d26512314" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" ADD CONSTRAINT "FK_cf1cdd4b148722b7f56719008ec" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" ADD CONSTRAINT "FK_ce876c18cfee282fea8bcc5abc1" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" ADD CONSTRAINT "FK_0b9714cb38c843b2a5ca45e6833" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "family_history" DROP CONSTRAINT "FK_0b9714cb38c843b2a5ca45e6833"`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" DROP CONSTRAINT "FK_ce876c18cfee282fea8bcc5abc1"`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" DROP CONSTRAINT "FK_cf1cdd4b148722b7f56719008ec"`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" DROP CONSTRAINT "FK_478c6031e7a954ea93d26512314"`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" DROP CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa"`,
    );
    await queryRunner.query(`DROP TABLE "family_history"`);
    await queryRunner.query(
      `DROP TYPE "public"."family_history_relation_enum"`,
    );
    await queryRunner.query(`DROP TABLE "patient"`);
    await queryRunner.query(`DROP TYPE "public"."patient_maritalstatus_enum"`);
    await queryRunner.query(`DROP TYPE "public"."patient_bloodtype_enum"`);
    await queryRunner.query(`DROP TYPE "public"."patient_gender_enum"`);
    await queryRunner.query(`DROP TABLE "emergency_contact"`);
    await queryRunner.query(
      `DROP TYPE "public"."emergency_contact_relation_enum"`,
    );
    await queryRunner.query(`DROP TABLE "surgery"`);
    await queryRunner.query(`DROP TABLE "chronic_disease"`);
    await queryRunner.query(`DROP TABLE "allergy"`);
  }
}
