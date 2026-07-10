import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddVoiceReports1783703374002 implements MigrationInterface {
  name = 'AddVoiceReports1783703374002';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TYPE "public"."voice_report_status_enum" AS ENUM('queued', 'processing', 'completed', 'failed')`,
    );
    await queryRunner.query(
      `CREATE TABLE "voice_report" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientUserId" integer NOT NULL, "doctorUserId" integer NOT NULL, "appointmentId" integer, "status" "public"."voice_report_status_enum" NOT NULL DEFAULT 'queued', "originalFileName" character varying NOT NULL, "originalMimeType" character varying NOT NULL, "audioUrl" character varying, "transcription" text, "clinicalReport" jsonb, "errorMessage" text, CONSTRAINT "PK_9f9e8f8b6c1d2a4b5e6f7a8b9c0" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "voice_report" ADD CONSTRAINT "FK_7a6b1c2d3e4f5a6b7c8d9e0f1a2" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "voice_report" ADD CONSTRAINT "FK_2b3c4d5e6f7a8b9c0d1e2f3a4b5" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "voice_report" ADD CONSTRAINT "FK_5c6d7e8f9a0b1c2d3e4f5a6b7c8" FOREIGN KEY ("appointmentId") REFERENCES "appointment"("id") ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "voice_report" DROP CONSTRAINT "FK_5c6d7e8f9a0b1c2d3e4f5a6b7c8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "voice_report" DROP CONSTRAINT "FK_2b3c4d5e6f7a8b9c0d1e2f3a4b5"`,
    );
    await queryRunner.query(
      `ALTER TABLE "voice_report" DROP CONSTRAINT "FK_7a6b1c2d3e4f5a6b7c8d9e0f1a2"`,
    );
    await queryRunner.query(`DROP TABLE "voice_report"`);
    await queryRunner.query(`DROP TYPE "public"."voice_report_status_enum"`);
  }
}
