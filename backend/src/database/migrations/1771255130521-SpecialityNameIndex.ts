import type { MigrationInterface, QueryRunner } from 'typeorm';

export class Migrations1771255130521 implements MigrationInterface {
  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE INDEX "idx_speciality_name_trgm" ON "speciality" USING GIN (name gin_trgm_ops)`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`DROP INDEX "idx_speciality_name_trgm"`);
  }
}
