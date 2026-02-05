import type { MigrationInterface, QueryRunner } from 'typeorm';

export class RemovePatientId1769950889778 implements MigrationInterface {
  name = 'RemovePatientId1769950889778';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "allergy" DROP CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa"`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" DROP CONSTRAINT "FK_478c6031e7a954ea93d26512314"`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" DROP CONSTRAINT "FK_0b9714cb38c843b2a5ca45e6833"`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" DROP CONSTRAINT "FK_ce876c18cfee282fea8bcc5abc1"`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" DROP CONSTRAINT "FK_cf1cdd4b148722b7f56719008ec"`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" RENAME COLUMN "patientId" TO "patientUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" RENAME COLUMN "patientId" TO "patientUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" RENAME COLUMN "patientId" TO "patientUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" RENAME COLUMN "patientId" TO "patientUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" RENAME COLUMN "id" TO "userId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" RENAME CONSTRAINT "PK_8dfa510bb29ad31ab2139fbfb99" TO "PK_6636aefca0bdad8933c7cc3e394"`,
    );
    await queryRunner.query(
      `ALTER SEQUENCE "patient_id_seq" RENAME TO "patient_userId_seq"`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" RENAME COLUMN "patientId" TO "patientUserId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" ALTER COLUMN "userId" DROP DEFAULT`,
    );
    await queryRunner.query(`DROP SEQUENCE "patient_userId_seq"`);
    await queryRunner.query(
      `ALTER TABLE "allergy" ADD CONSTRAINT "FK_e3a90c5c482b92b0177444ae29b" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" ADD CONSTRAINT "FK_40a34f9b285a01a9eb35c36d32f" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" ADD CONSTRAINT "FK_392dc4103f2b41957f9dc9fd2d0" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" ADD CONSTRAINT "FK_c3a9026a245ccbab5fea488e58e" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" ADD CONSTRAINT "FK_6636aefca0bdad8933c7cc3e394" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" ADD CONSTRAINT "FK_48415dcb09e2f4e20e9a4894e8a" FOREIGN KEY ("patientUserId") REFERENCES "patient"("userId") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "surgery" DROP CONSTRAINT "FK_48415dcb09e2f4e20e9a4894e8a"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" DROP CONSTRAINT "FK_6636aefca0bdad8933c7cc3e394"`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" DROP CONSTRAINT "FK_c3a9026a245ccbab5fea488e58e"`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" DROP CONSTRAINT "FK_392dc4103f2b41957f9dc9fd2d0"`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" DROP CONSTRAINT "FK_40a34f9b285a01a9eb35c36d32f"`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" DROP CONSTRAINT "FK_e3a90c5c482b92b0177444ae29b"`,
    );
    await queryRunner.query(
      `CREATE SEQUENCE IF NOT EXISTS "patient_userId_seq" OWNED BY "patient"."userId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" ALTER COLUMN "userId" SET DEFAULT nextval('"patient_userId_seq"')`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" RENAME COLUMN "patientUserId" TO "patientId"`,
    );
    await queryRunner.query(
      `ALTER SEQUENCE "patient_userId_seq" RENAME TO "patient_id_seq"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" RENAME CONSTRAINT "PK_6636aefca0bdad8933c7cc3e394" TO "PK_8dfa510bb29ad31ab2139fbfb99"`,
    );
    await queryRunner.query(
      `ALTER TABLE "patient" RENAME COLUMN "userId" TO "id"`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" RENAME COLUMN "patientUserId" TO "patientId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" RENAME COLUMN "patientUserId" TO "patientId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" RENAME COLUMN "patientUserId" TO "patientId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" RENAME COLUMN "patientUserId" TO "patientId"`,
    );
    await queryRunner.query(
      `ALTER TABLE "surgery" ADD CONSTRAINT "FK_cf1cdd4b148722b7f56719008ec" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "emergency_contact" ADD CONSTRAINT "FK_ce876c18cfee282fea8bcc5abc1" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "family_history" ADD CONSTRAINT "FK_0b9714cb38c843b2a5ca45e6833" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "chronic_disease" ADD CONSTRAINT "FK_478c6031e7a954ea93d26512314" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
    await queryRunner.query(
      `ALTER TABLE "allergy" ADD CONSTRAINT "FK_bc3502f7acc8a5575b7185429fa" FOREIGN KEY ("patientId") REFERENCES "patient"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }
}
