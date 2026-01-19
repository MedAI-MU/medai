import { Body, Controller, HttpCode, HttpStatus, Post } from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { CreateDocScheduleTemplateDto } from './dtos/create-doctor-schedule-template.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

@Controller('schedules/templates')
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
  ) {}

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async create(
    @CurrentUser() currentUser: TokenUser,
    @Body() createDocScheduleTemplateDto: CreateDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.create(
      createDocScheduleTemplateDto,
      currentUser.id,
    );
  }
}
