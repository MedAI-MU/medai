import type { MigrationInterface, QueryRunner } from 'typeorm';

export class DoctorSpeciality1771253574474 implements MigrationInterface {
  name = 'DoctorSpeciality1771253574474';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "speciality" ("id" SERIAL NOT NULL, "name" character varying NOT NULL, CONSTRAINT "PK_cfdbcfa372a34f2d9c1d5180052" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "doctor_speciality" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "isPrimary" boolean NOT NULL, "yearsOfExperience" integer NOT NULL, "doctorUserId" integer, "specialityId" integer, CONSTRAINT "PK_2017cd46b15edd20c925573dca3" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(`ALTER TABLE "doctor" DROP COLUMN "specialty"`);
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD "createdAt" TIMESTAMP NOT NULL DEFAULT now()`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD "updatedAt" TIMESTAMP NOT NULL DEFAULT now()`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" ADD CONSTRAINT "FK_924563c228f70355047837af96d" FOREIGN KEY ("specialityId") REFERENCES "speciality"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_924563c228f70355047837af96d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor_speciality" DROP CONSTRAINT "FK_b6d5fdebf2912a4edc441ddd275"`,
    );
    await queryRunner.query(`ALTER TABLE "doctor" DROP COLUMN "updatedAt"`);
    await queryRunner.query(`ALTER TABLE "doctor" DROP COLUMN "createdAt"`);
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD "specialty" character varying NOT NULL`,
    );
    await queryRunner.query(`DROP TABLE "doctor_speciality"`);
    await queryRunner.query(`DROP TABLE "speciality"`);
  }
}
