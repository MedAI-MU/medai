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
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';

@Controller('doctors/schedules')
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
    private readonly scheduleSlotsService: DocScheduleSlotsService,
  ) {}

  @Post('templates')
  @HttpCode(HttpStatus.CREATED)
  async createTemplate(
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
  async updateTemplate(
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
  async deleteTemplate(@Param('id') id: number) {
    await this.scheduleTemplatesService.delete(id);
  }

  @Post()
  @HttpCode(HttpStatus.CREATED)
  async createSlots(
    @CurrentUser() currentUser: TokenUser,
    @Body() createDocScheduleDto: CreateDocScheduleDto,
  ) {
    await this.scheduleSlotsService.create(
      createDocScheduleDto,
      currentUser.id,
    );
  }

  @Patch('slots/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async updateSlot(
    @Body() updateDocScheduleSlotDto: UpdateDocScheduleSlotDto,
    @Param('id') id: number,
  ) {
    await this.scheduleSlotsService.update(updateDocScheduleSlotDto, id);
  }
}
