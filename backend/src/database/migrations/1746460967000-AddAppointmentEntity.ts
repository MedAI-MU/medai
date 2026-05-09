import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddAppointmentEntity1746460967000 implements MigrationInterface {
  name = 'AddAppointmentEntity1746460967000';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TYPE "public"."appointment_status_enum" AS ENUM('pending', 'confirmed', 'cancelled')`,
    );
    await queryRunner.query(
      `CREATE TABLE "appointment" (
        "createdAt" TIMESTAMP NOT NULL DEFAULT now(),
        "updatedAt" TIMESTAMP NOT NULL DEFAULT now(),
        "id" SERIAL NOT NULL,
        "patientUserId" integer NOT NULL,
        "doctorUserId" integer NOT NULL,
        "scheduleSlotId" integer NOT NULL,
        "status" "public"."appointment_status_enum" NOT NULL DEFAULT 'pending',
        "confirmedByUserId" integer,
        CONSTRAINT "REL_appointment_scheduleSlot" UNIQUE ("scheduleSlotId"),
        CONSTRAINT "PK_appointment" PRIMARY KEY ("id")
      )`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment"
        ADD CONSTRAINT "FK_appointment_patient"
        FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId")
        ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment"
        ADD CONSTRAINT "FK_appointment_doctor"
        FOREIGN KEY ("doctorUserId") REFERENCES "doctor"("userId")
        ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment"
        ADD CONSTRAINT "FK_appointment_scheduleSlot"
        FOREIGN KEY ("scheduleSlotId") REFERENCES "doc_schedule_slot"("id")
        ON DELETE RESTRICT ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment"
        ADD CONSTRAINT "FK_appointment_confirmedBy"
        FOREIGN KEY ("confirmedByUserId") REFERENCES "user"("id")
        ON DELETE SET NULL ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_appointment_confirmedBy"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_appointment_scheduleSlot"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_appointment_doctor"`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" DROP CONSTRAINT "FK_appointment_patient"`,
    );
    await queryRunner.query(`DROP TABLE "appointment"`);
    await queryRunner.query(`DROP TYPE "public"."appointment_status_enum"`);
  }
}
