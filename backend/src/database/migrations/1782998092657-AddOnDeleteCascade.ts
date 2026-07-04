import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddOnDeleteCascade1782998092657 implements MigrationInterface {
  name = 'AddOnDeleteCascade1782998092657';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" DROP CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_152208a8d8a832b2e538c1066bc"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" ADD CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_152208a8d8a832b2e538c1066bc" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" DROP CONSTRAINT "FK_152208a8d8a832b2e538c1066bc"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" DROP CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_template" ADD CONSTRAINT "FK_152208a8d8a832b2e538c1066bc" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doc_schedule" ADD CONSTRAINT "FK_8ba85d91e3edb8e2b8bd3cc3e2d" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }
}
