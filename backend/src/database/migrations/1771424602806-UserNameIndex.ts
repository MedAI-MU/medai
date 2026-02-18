import type { MigrationInterface, QueryRunner } from 'typeorm';

export class UserNameIndex1771424602806 implements MigrationInterface {
  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE INDEX "idx_user_name_trgm" ON "user" USING GIN (name gin_trgm_ops)`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`DROP INDEX "idx_user_name_trgm"`);
  }
}
