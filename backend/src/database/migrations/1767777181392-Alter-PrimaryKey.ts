import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AlterPrimaryKey1767777181392 implements MigrationInterface {
  name = 'AlterPrimaryKey1767777181392';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "PK_ee6bf6c8de78803212c548fcb94"`,
    );
    await queryRunner.query(`ALTER TABLE "doctor" DROP COLUMN "id"`);
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ALTER COLUMN "userId" SET NOT NULL`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "PK_e573a17ab8b6eea2b7fe9905fa8" PRIMARY KEY ("userId")`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "REL_e573a17ab8b6eea2b7fe9905fa"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "REL_e573a17ab8b6eea2b7fe9905fa" UNIQUE ("userId")`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "PK_e573a17ab8b6eea2b7fe9905fa8"`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ALTER COLUMN "userId" DROP NOT NULL`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
    await queryRunner.query(`ALTER TABLE "doctor" ADD "id" SERIAL NOT NULL`);
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "PK_ee6bf6c8de78803212c548fcb94" PRIMARY KEY ("id")`,
    );
  }
}
