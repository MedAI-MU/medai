import type { MigrationInterface, QueryRunner } from 'typeorm';

export class ChangeSecretaryIdToCreatedByUserId1769902572531 implements MigrationInterface {
  name = 'ChangeSecretaryIdToCreatedByUserId1769902572531';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_b4c5235770ed6445918a21f8cbc"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_1b852f9cf6bf2e5bb1e8735ed00"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" RENAME COLUMN "secretaryId" TO "createdByUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" RENAME COLUMN "secretaryId" TO "createdByUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_1df25989ef802939a06afba341c" FOREIGN KEY ("createdByUserId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_0d73eb7cfbce11784c127284bfd" FOREIGN KEY ("createdByUserId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "FK_0d73eb7cfbce11784c127284bfd"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_1df25989ef802939a06afba341c"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" RENAME COLUMN "createdByUserId" TO "secretaryId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" RENAME COLUMN "createdByUserId" TO "secretaryId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "FK_1b852f9cf6bf2e5bb1e8735ed00" FOREIGN KEY ("secretaryId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_b4c5235770ed6445918a21f8cbc" FOREIGN KEY ("secretaryId") REFERENCES "user"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }
}
