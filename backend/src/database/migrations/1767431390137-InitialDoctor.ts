import type { MigrationInterface, QueryRunner } from 'typeorm';

export class InitialDoctor1767431390137 implements MigrationInterface {
  name = 'InitialDoctor1767431390137';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "doctor" ("id" SERIAL NOT NULL, "specialty" character varying NOT NULL, "userId" integer, CONSTRAINT "REL_e573a17ab8b6eea2b7fe9905fa" UNIQUE ("userId"), CONSTRAINT "PK_ee6bf6c8de78803212c548fcb94" PRIMARY KEY ("id"))`,
    );
    await queryRunner.query(
      `ALTER TABLE "doctor" ADD CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8" FOREIGN KEY ("userId") REFERENCES "user"("id") ON DELETE NO ACTION ON UPDATE NO ACTION`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doctor" DROP CONSTRAINT "FK_e573a17ab8b6eea2b7fe9905fa8"`,
    );
    await queryRunner.query(`DROP TABLE "doctor"`);
  }
}
