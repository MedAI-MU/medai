import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddVerficationEmailExpiresAt1783410685729 implements MigrationInterface {
  name = 'AddVerficationEmailExpiresAt1783410685729';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD "verificationTokenExpiresAt" TIMESTAMP`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" DROP COLUMN "verificationTokenExpiresAt"`,
    );
  }
}
