import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddOnDeleteCascadeSpeciality1783565271285 implements MigrationInterface {
  name = 'AddOnDeleteCascadeSpeciality1783565271285';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_924563c228f70355047837af96d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_924563c228f70355047837af96d" FOREIGN KEY ("specialityId") REFERENCES "speciality"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_924563c228f70355047837af96d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_924563c228f70355047837af96d" FOREIGN KEY ("specialityId") REFERENCES "speciality"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
  }
}
