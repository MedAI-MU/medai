import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddScansAndDiagnosis1783182058254 implements MigrationInterface {
  name = 'AddScansAndDiagnosis1783182058254';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "scan_image" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "scanId" integer NOT NULL, "path" character varying NOT NULL, CONSTRAINT "PK_4802150bbf9581ad193cb15ba88" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "report" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientUserId" integer NOT NULL, "scanId" integer, "path" character varying NOT NULL, CONSTRAINT "PK_99e4d0bea58cba73c57f935a546" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "scan" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientUserId" integer NOT NULL, "appointmentId" integer, CONSTRAINT "PK_9868a638d0569ba3fe3bddcef84" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `CREATE TABLE "diagnosis" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientUserId" integer NOT NULL, "doctorUserId" integer NOT NULL, "appointmentId" integer NOT NULL, "symptoms" text NOT NULL, "summary" text NOT NULL, CONSTRAINT "REL_589f5bdaa8b34421a72ca8bb3a" UNIQUE ("appointmentId"), CONSTRAINT "PK_d5dbb1cc4e30790df368da56961" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan_image" ADD CONSTRAINT "FK_44d000fdbdcfebae8368b0dcdde" FOREIGN KEY ("scanId") REFERENCES "scan"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD CONSTRAINT "FK_dfc931d77a0f1819ebeebc47d95" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" ADD CONSTRAINT "FK_0df47cd076d050c85635ff56844" FOREIGN KEY ("scanId") REFERENCES "scan"("id") ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan" ADD CONSTRAINT "FK_4dcc87bb54556fd1e5a64f0343c" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan" ADD CONSTRAINT "FK_697a8831619a779a9ee714262d1" FOREIGN KEY ("appointmentId") REFERENCES "appointment"("id") ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "diagnosis" ADD CONSTRAINT "FK_af7c3657b5fa3ac534b2addd271" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "diagnosis" ADD CONSTRAINT "FK_6df3e63e6bc18faed71f15303d9" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "diagnosis" ADD CONSTRAINT "FK_589f5bdaa8b34421a72ca8bb3a6" FOREIGN KEY ("appointmentId") REFERENCES "appointment"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "diagnosis" DROP CONSTRAINT "FK_589f5bdaa8b34421a72ca8bb3a6"`,
    );
    await queryRunner.query(
      `ALTER TABLE "diagnosis" DROP CONSTRAINT "FK_6df3e63e6bc18faed71f15303d9"`,
    );
    await queryRunner.query(
      `ALTER TABLE "diagnosis" DROP CONSTRAINT "FK_af7c3657b5fa3ac534b2addd271"`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan" DROP CONSTRAINT "FK_697a8831619a779a9ee714262d1"`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan" DROP CONSTRAINT "FK_4dcc87bb54556fd1e5a64f0343c"`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" DROP CONSTRAINT "FK_0df47cd076d050c85635ff56844"`,
    );
    await queryRunner.query(
      `ALTER TABLE "report" DROP CONSTRAINT "FK_dfc931d77a0f1819ebeebc47d95"`,
    );
    await queryRunner.query(
      `ALTER TABLE "scan_image" DROP CONSTRAINT "FK_44d000fdbdcfebae8368b0dcdde"`,
    );
    await queryRunner.query(`DROP TABLE "diagnosis"`);
    await queryRunner.query(`DROP TABLE "scan"`);
    await queryRunner.query(`DROP TABLE "report"`);
    await queryRunner.query(`DROP TABLE "scan_image"`);
  }
}
