import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddAppointment1779292037718 implements MigrationInterface {
  name = 'AddAppointment1779292037718';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TYPE "public"."appointment_status_enum" AS ENUM('pending', 'confirmed', 'cancelled', 'finished')`,
    );
    await queryRunner.query(
      `CREATE TABLE "appointment" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientUserId" integer NOT NULL, "doctorUserId" integer NOT NULL, "scheduleSlotId" integer NOT NULL, "status" "public"."appointment_status_enum" NOT NULL DEFAULT 'pending', "confirmedByUserId" integer, "rating" integer, "review" text, CONSTRAINT "REL_7019088d37df9c67fc24c87247" UNIQUE ("scheduleSlotId"), CONSTRAINT "PK_e8be1a53027415e709ce8a2db74" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_b98383603e21313a38c016f8c78" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_f00b5de249813d0d75a71e21344" FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_7019088d37df9c67fc24c872476" FOREIGN KEY ("scheduleSlotId") REFERENCES "doc_schedule_slot"("id") ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ADD CONSTRAINT "FK_c601e12d6c4cb3bdb1d2d379768" FOREIGN KEY ("confirmedByUserId") REFERENCES "user"("id") ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_c601e12d6c4cb3bdb1d2d379768"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_7019088d37df9c67fc24c872476"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_f00b5de249813d0d75a71e21344"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_b98383603e21313a38c016f8c78"`,
    );
    await queryRunner.query(`DROP TABLE "appointment"`);
    await queryRunner.query(`DROP TYPE "public"."appointment_status_enum"`);
  }
}
