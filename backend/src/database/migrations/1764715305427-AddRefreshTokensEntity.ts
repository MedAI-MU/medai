import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddRefreshTokensEntity1764715305427 implements MigrationInterface {
  name = 'AddRefreshTokensEntity1764715305427';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "refresh_token" ("id" SERIAL NOT NULL, "refreshToken" character varying NOT NULL, "expiresAt" TIMESTAMP NOT NULL, "userId" integer, CONSTRAINT "PK_b575dd3c21fb0831013c909e7fe" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "refresh_token" ADD CONSTRAINT "FK_8e913e288156c133999341156ad" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE CASCADE ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "refresh_token" DROP CONSTRAINT "FK_8e913e288156c133999341156ad"`,
    );
    await queryRunner.query(`DROP TABLE "refresh_token"`);
  }
}
