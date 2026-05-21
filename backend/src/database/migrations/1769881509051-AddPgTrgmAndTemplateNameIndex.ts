import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddPgTrgmAndTemplateNameIndex1769881509051 implements MigrationInterface {
  public async up(queryRunner: QueryRunner): Promise<void> {
    // Enable extension if not exists
    await queryRunner.query(`CREATE EXTENSION IF NOT EXISTS pg_trgm;`);

    // Create GIN index for fast ILIKE '%search%'
    await queryRunner.query(`
            CREATE INDEX idx_template_name_trgm
            ON doc_schedule_template
            USING gin (name gin_trgm_ops);
        `);
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`DROP INDEX IF EXISTS idx_template_name_trgm;`);
    // Optionally drop extension if you want (rarely needed)
    // await queryRunner.query(`DROP EXTENSION IF EXISTS pg_trgm;`);
  }
}
