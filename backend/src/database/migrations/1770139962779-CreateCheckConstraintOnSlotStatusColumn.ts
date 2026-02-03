import type { MigrationInterface, QueryRunner } from 'typeorm';
const SlotStatuses = ['available', 'booked', 'cancelled', 'completed'];
export class CreateCheckConstraintOnSlotStatusColumn1770139962779
  implements MigrationInterface
{
  public async up(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" ADD CONSTRAINT "CHK_doc_schedule_slot_status_valid_values" CHECK (status IN (${SlotStatuses.map(
        (status) => `'${status}'`,
      ).join(', ')}))`,
    );
  }

  public async down(queryRunner: QueryRunner): Promise<void> {
    await queryRunner.query(
      `ALTER TABLE "doc_schedule_slot" DROP CONSTRAINT "CHK_doc_schedule_slot_status_valid_values"`,
    );
  }
}
