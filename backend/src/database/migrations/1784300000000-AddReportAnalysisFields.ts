import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddReportAnalysisFields1784300000000 implements MigrationInterface {
  name = 'AddReportAnalysisFields1784300000000';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TYPE "public"."report_analysisStatus_enum" AS ENUM('queued', 'processing', 'completed', 'failed')`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD COLUMN "appointmentId" integer`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD COLUMN "analysisStatus" "public"."report_analysisStatus_enum"`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD COLUMN "analysisResult" jsonb`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD COLUMN "analysisError" text`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD CONSTRAINT "FK_report_appointmentId" FOREIGN KEY ("appointmentId") REFERENCES "appointment"("id") ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "report" DROP CONSTRAINT "FK_report_appointmentId"`,
    );
    await queryRunner.query(`ALTER TABLE "report" DROP COLUMN "analysisError"`);
    await queryRunner.query(
      `ALTER TABLE "report" DROP COLUMN "analysisResult"`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" DROP COLUMN "analysisStatus"`,
    );
    await queryRunner.query(`ALTER TABLE "report" DROP COLUMN "appointmentId"`);
    await queryRunner.query(`DROP TYPE "public"."report_analysisStatus_enum"`);
  }
}
