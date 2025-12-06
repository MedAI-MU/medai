import type { MigrationInterface, QueryRunner } from 'typeorm';

export class RemoveHashedRTIndexAndRenameIt1765051744087
  implements MigrationInterface
{
  name = 'RemoveHashedRTIndexAndRenameIt1765051744087';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `DROP INDEX "public"."IDX_a1cf2d54ba0052e328c829b01d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "refresh_token" RENAME COLUMN "hashedRefreshToken" TO "token"`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "refresh_token" RENAME COLUMN "token" TO "hashedRefreshToken"`,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_a1cf2d54ba0052e328c829b01d" ON "refresh_token" ("hashedRefreshToken") `,
    );
  }
}
