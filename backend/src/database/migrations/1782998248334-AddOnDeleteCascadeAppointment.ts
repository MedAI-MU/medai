import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddOnDeleteCascadeAppointment1782998248334 implements MigrationInterface {
  name = 'AddOnDeleteCascadeAppointment1782998248334';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_f00b5de249813d0d75a71e21344"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_f00b5de249813d0d75a71e21344" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_f00b5de249813d0d75a71e21344"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_f00b5de249813d0d75a71e21344" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
  }
}
