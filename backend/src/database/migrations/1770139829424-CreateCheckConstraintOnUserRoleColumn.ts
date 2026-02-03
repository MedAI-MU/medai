import type { MigrationInterface, QueryRunner } from 'typeorm';

const UserRoles = ['doctor', 'patient', 'secretary'];

export class CreateCheckConstraintOnUserRoleColumn1770139829424
  implements MigrationInterface
{
  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" ADD CONSTRAINT "CHK_user_role_valid_values" CHECK (role IN (${UserRoles.map(
        (role) => `'${role}'`,
      ).join(', ')}))`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "user" DROP CONSTRAINT "CHK_user_role_valid_values"`,
    );
  }
}
