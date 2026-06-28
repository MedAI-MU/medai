import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddDoctorScheduleEntities1769091005458 implements MigrationInterface {
  name = 'AddDoctorScheduleEntities1769091005458';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "doc_schedule_template_slot" ("id" SERIAL NOT NULL, "weekDay" integer NOT NULL, "startTime" TIME NOT NULL, "endTime" TIME NOT NULL, "templateId" integer, CONSTRAINT "PK_ae72c7c0b2a3a2cb72403492fb8" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "doc_schedule_template" ("id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "doctorId" integer, "secretaryId" integer, CONSTRAINT "PK_d8d749caefcabd5af5c68f2b7b8" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "doc_schedule_slot" ("id" SERIAL NOT NULL, "dayDate" date NOT NULL, "startTime" TIME NOT NULL, "endTime" TIME NOT NULL, "status" character varying(20) NOT NULL DEFAULT 'available', "doctorId" integer, "secretaryId" integer, CONSTRAINT "PK_498b15a157597bd1ad14c6d4ea5" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template_slot" ADD CONSTRAINT "FK_40f45c3dd9330ea04dbcd60231a" FOREIGN KEY ("templateId") REFERENCES "doc_schedule_template"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_152208a8d8a832b2e538c1066bc" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_b4c5235770ed6445918a21f8cbc" FOREIGN KEY ("secretaryId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_fdf81b56112f00fef606d6720c8" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_1b852f9cf6bf2e5bb1e8735ed00" FOREIGN KEY ("secretaryId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_1b852f9cf6bf2e5bb1e8735ed00"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_fdf81b56112f00fef606d6720c8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_b4c5235770ed6445918a21f8cbc"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_152208a8d8a832b2e538c1066bc"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template_slot" DROP CONSTRAINT "FK_40f45c3dd9330ea04dbcd60231a"`,
    );
    await queryRunner.query(`DROP TABLE "doc_schedule_slot"`);
    await queryRunner.query(`DROP TABLE "doc_schedule_template"`);
    await queryRunner.query(`DROP TABLE "doc_schedule_template_slot"`);
  }
}
