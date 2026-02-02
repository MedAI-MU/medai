import {
  Body,
  Controller,
  Delete,
  Get,
  HttpCode,
  HttpStatus,
  Param,
  ParseIntPipe,
  Patch,
  Post,
  Query,
} from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';
import { PagedListDto } from '../shared/dtos/paged-list.dto';

@Controller('doctors')
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
    private readonly scheduleSlotsService: DocScheduleSlotsService,
  ) {}

  @Get('schedule-templates')
  @HttpCode(HttpStatus.OK)
  async getAllTemplates(
    @CurrentUser() currentUser: TokenUser,
    @Query('pageNo', new ParseIntPipe({ optional: true })) pageNo = 1,
    @Query('pageSize', new ParseIntPipe({ optional: true })) pageSize = 10,
    @Query('name') name?: string,
    @Query('doctorId', new ParseIntPipe({ optional: true })) doctorId?: number,
  ) {
    const { data, total } = await this.scheduleTemplatesService.getAll(
      currentUser,
      pageNo,
      pageSize,
      name,
      doctorId,
    );
    return new PagedListDto(data, total, pageNo, pageSize);
  }

  @Post(':doctorId/schedule-templates')
  @HttpCode(HttpStatus.CREATED)
  async createTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Body() createDocScheduleTemplateDto: CreateDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.create(
      doctorId,
      createDocScheduleTemplateDto,
      currentUser,
    );
  }

  @Patch(':doctorId/schedule-templates/:templateId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async updateTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Param('templateId', ParseIntPipe) templateId: number,
    @Body() updateDocScheduleTemplateDto: UpdateDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.update(
      updateDocScheduleTemplateDto,
      templateId,
      doctorId,
      currentUser,
    );
  }

  @Delete(':doctorId/schedule-templates/:templateId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deleteTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Param('templateId', ParseIntPipe) templateId: number,
  ) {
    await this.scheduleTemplatesService.delete(
      templateId,
      doctorId,
      currentUser,
    );
  }

  @Get(':doctorId/schedule-slots')
  @HttpCode(HttpStatus.OK)
  async getDoctorSlots(
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Query('fromDate') fromDate?: string,
    @Query('toDate') toDate?: string,
    @Query('pageNo', new ParseIntPipe({ optional: true })) pageNo: number = 1,
    @Query('pageSize', new ParseIntPipe({ optional: true }))
    pageSize: number = 10,
  ) {
    return this.scheduleSlotsService.getByDoctorId(
      doctorId,
      fromDate,
      toDate,
      pageNo,
      pageSize,
    );
  }

  @Post(':doctorId/schedule-slots')
  @HttpCode(HttpStatus.CREATED)
  async createSlots(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Body() createDocScheduleDto: CreateDocScheduleDto,
  ) {
    await this.scheduleSlotsService.create(
      doctorId,
      createDocScheduleDto,
      currentUser,
    );
  }

  @Patch(':doctorId/schedule-slots/:slotId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async updateSlot(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Param('slotId', ParseIntPipe) slotId: number,
    @Body() updateDocScheduleSlotDto: UpdateDocScheduleSlotDto,
  ) {
    await this.scheduleSlotsService.update(
      updateDocScheduleSlotDto,
      slotId,
      doctorId,
      currentUser,
    );
  }

  @Delete(':doctorId/schedule-slots/:slotId')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deleteSlot(
    @CurrentUser() currentUser: TokenUser,
    @Param('doctorId', ParseIntPipe) doctorId: number,
    @Param('slotId', ParseIntPipe) slotId: number,
  ) {
    await this.scheduleSlotsService.delete(slotId, doctorId, currentUser);
  }
}
