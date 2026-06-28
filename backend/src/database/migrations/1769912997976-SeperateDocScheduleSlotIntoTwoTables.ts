import type { MigrationInterface, QueryRunner } from 'typeorm';

export class SeperateDocScheduleSlotIntoTwoTables1769912997976 implements MigrationInterface {
  name = 'SeperateDocScheduleSlotIntoTwoTables1769912997976';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_fdf81b56112f00fef606d6720c8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_0d73eb7cfbce11784c127284bfd"`,
    );
    await queryRunner.query(
      `CREATE TABLE "doc_schedule" ("id" SERIAL NOT NULL, "dayDate" date NOT NULL, "doctorId" integer, "createdByUserId" integer, CONSTRAINT "PK_e9be0994e26b1bacbaec24f7376" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP COLUMN "dayDate"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP COLUMN "doctorId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP COLUMN "createdByUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD "docScheduleId" integer`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_359bfc8d2a9617bab3f221e1b03" FOREIGN KEY ("docScheduleId") REFERENCES "doc_schedule"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" ADD CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" ADD CONSTRAINT "FK_8d377419a16e8e5ba204774ce8a" FOREIGN KEY ("createdByUserId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" DROP CONSTRAINT "FK_8d377419a16e8e5ba204774ce8a"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" DROP CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_359bfc8d2a9617bab3f221e1b03"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP COLUMN "docScheduleId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD "createdByUserId" integer`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD "doctorId" integer`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD "dayDate" date NOT NULL`,
    );
    await queryRunner.query(`DROP TABLE "doc_schedule"`);
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_0d73eb7cfbce11784c127284bfd" FOREIGN KEY ("createdByUserId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_fdf81b56112f00fef606d6720c8" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }
}
