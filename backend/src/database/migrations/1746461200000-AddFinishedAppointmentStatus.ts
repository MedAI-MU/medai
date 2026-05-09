import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddFinishedAppointmentStatus1746461200000
  implements MigrationInterface
{
  name = 'AddFinishedAppointmentStatus1746461200000';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TYPE "public"."appointment_status_enum" ADD VALUE 'finished'`,
    );
  }

  // PostgreSQL does not support removing enum values directly.
  // To roll back, the enum must be recreated without 'finished'.
  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "appointment" ALTER COLUMN "status" TYPE varchar USING status::text`,
    );
    await queryRunner.query(`DROP TYPE "public"."appointment_status_enum"`);
    await queryRunner.query(
      `CREATE TYPE "public"."appointment_status_enum" AS ENUM('pending', 'confirmed', 'cancelled')`,
    );
    await queryRunner.query(
      `ALTER TABLE "appointment" ALTER COLUMN "status" TYPE "public"."appointment_status_enum" USING status::"public"."appointment_status_enum"`,
    );
  }
}
