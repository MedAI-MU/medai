import type { MigrationInterface, QueryRunner } from 'typeorm';

export class AddUserEntity1764710856340 implements MigrationInterface {
  name = 'AddUserEntity1764710856340';

  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `CREATE TABLE "user" ("id" SERIAL NOT NULL, "name" character varying(100) NOT NULL, "email" character varying(256) NOT NULL, "password" character varying NOT NULL, "phone" character varying(20) NOT NULL, "createdAt" TIMESTAMP NOT NULL DEFAULT now(), "updatedAt" TIMESTAMP NOT NULL DEFAULT now(), CONSTRAINT "UQ_065d4d8f3b5adb4a08841eae3c8" UNIQUE ("name"), CONSTRAINT "UQ_e12875dfb3b1d92d7d7c5377e22" UNIQUE ("email"), CONSTRAINT "PK_cace4a159ff9f2512dd42373760" PRIMARY KEY ("id"))`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(`DROP TABLE "user"`);
  }
}
