import type { MigrationInterface, QueryRunner } from 'typeorm';

export class RenameAndIndexRefreshTokenColumn1764716507750
  implements MigrationInterface
{
  name = 'RenameAndIndexRefreshTokenColumn1764716507750';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "refresh_token" RENAME COLUMN "refreshToken" TO "hashedRefreshToken"`,
    );
    await queryRunner.query(
      `CREATE INDEX "IDX_a1cf2d54ba0052e328c829b01d" ON "refresh_token" ("hashedRefreshToken") `,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `DROP INDEX "public"."IDX_a1cf2d54ba0052e328c829b01d"`,
    );
    await queryRunner.query(
      `ALTER TABLE "refresh_token" RENAME COLUMN "hashedRefreshToken" TO "refreshToken"`,
    );
  }
}
