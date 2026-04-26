import { MigrationInterface, QueryRunner } from "typeorm";

export class AddAppointments1777229298605 implements MigrationInterface {
    name = 'AddAppointments1777229298605'

    public async up(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`CREATE TYPE "public"."appointment_status_enum" AS ENUM('pending', 'confirmed', 'completed', 'cancelled')`);
        await queryRunner.query(`CREATE TABLE "appointment" ("createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), "id" SERIAL NOT NULL, "patientId" integer NOT NULL, "doctorId" integer NOT NULL, "slotId" integer NOT NULL, "status" "public"."appointment_status_enum" NOT NULL DEFAULT 'pending', "bookedForName" character varying(100), "bookedForAge" character varying(20), "bookedForGender" character varying(20), "problemDescription" text, "cancellationReason" text, "rating" integer, "reviewComment" text, CONSTRAINT "PK_e8be1a53027415e709ce8a2db74" PRIMARY KEY ("id"))`);
        await queryRunner.query(`CREATE INDEX "IDX_5ce4c3130796367c93cd817948" ON "appointment" ("patientId") `);
        await queryRunner.query(`CREATE INDEX "IDX_514bcc3fb1b8140f85bf1cde6e" ON "appointment" ("doctorId") `);
        await queryRunner.query(`CREATE INDEX "IDX_b463fce395ead7791607a5c33e" ON "appointment" ("slotId") `);
        await queryRunner.query(`ALTER TABLE "appointment" ADD CONSTRAINT "FK_5ce4c3130796367c93cd817948e" FOREIGN KEY ("patientId") REFERENCES "patient"("userId") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "appointment" ADD CONSTRAINT "FK_514bcc3fb1b8140f85bf1cde6e2" FOREIGN KEY ("doctorId") REFERENCES "doctor"("userId") ON DELETE NO ACTION ON UPDATE NO ACTION`);
        await queryRunner.query(`ALTER TABLE "appointment" ADD CONSTRAINT "FK_b463fce395ead7791607a5c33eb" FOREIGN KEY ("slotId") REFERENCES "doc_schedule_slot"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`);
    }

    public async down(queryRunner: QueryRunner): Promise<void> {
        await queryRunner.query(`ALTER TABLE "appointment" DROP CONSTRAINT "FK_b463fce395ead7791607a5c33eb"`);
        await queryRunner.query(`ALTER TABLE "appointment" DROP CONSTRAINT "FK_514bcc3fb1b8140f85bf1cde6e2"`);
        await queryRunner.query(`ALTER TABLE "appointment" DROP CONSTRAINT "FK_5ce4c3130796367c93cd817948e"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_b463fce395ead7791607a5c33e"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_514bcc3fb1b8140f85bf1cde6e"`);
        await queryRunner.query(`DROP INDEX "public"."IDX_5ce4c3130796367c93cd817948"`);
        await queryRunner.query(`DROP TABLE "appointment"`);
        await queryRunner.query(`DROP TYPE "public"."appointment_status_enum"`);
    }

}
