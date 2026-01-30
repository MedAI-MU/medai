import {
  Body,
  Controller,
  Delete,
  HttpCode,
  HttpStatus,
  Param,
  Patch,
  Post,
} from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';

@Controller('doctors/schedules')
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
  ) {}

  @Post('templates')
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

  @Patch('templates/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async update(
    @Body() updateDocScheduleTemplateDto: UpdateDocScheduleTemplateDto,
    @Param('id') id: number,
  ) {
    await this.scheduleTemplatesService.update(
      updateDocScheduleTemplateDto,
      id,
    );
  }

  @Delete('templates/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async delete(@Param('id') id: number) {
    await this.scheduleTemplatesService.delete(id);
  }
}
