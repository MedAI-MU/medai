import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddIndexesToScheduleEntities1770072886852
  implements MigrationInterface
{
  name = 'AddIndexesToScheduleEntities1770072886852';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE INDEX "IDX_doc_schedule_template_slot_template" ON "doc_schedule_template_slot" ("templateId") `,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_doc_schedule_template_doctor_created" ON "doc_schedule_template" ("doctorId", "createdAt") `,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_doc_schedule_slot_schedule_status" ON "doc_schedule_slot" ("docScheduleId", "status") `,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_doc_schedule_day_date" ON "doc_schedule" ("dayDate") `,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_doc_schedule_doctor_day" ON "doc_schedule" ("doctorId", "dayDate") `,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `DROP INDEX "public"."IDX_doc_schedule_doctor_day"`,
    );
    await queryRunner.query(`DROP INDEX "public"."IDX_doc_schedule_day_date"`);
    await queryRunner.query(
      `DROP INDEX "public"."IDX_doc_schedule_slot_schedule_status"`,
    );
    await queryRunner.query(
      `DROP INDEX "public"."IDX_doc_schedule_template_doctor_created"`,
    );
    await queryRunner.query(
      `DROP INDEX "public"."IDX_doc_schedule_template_slot_template"`,
    );
  }
}
