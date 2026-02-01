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

@Controller('doctors/schedules')
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
    private readonly scheduleSlotsService: DocScheduleSlotsService,
  ) {}

  @Get('templates')
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

  @Post('templates')
  @HttpCode(HttpStatus.CREATED)
  async createTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Body() createDocScheduleTemplateDto: CreateDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.create(
      createDocScheduleTemplateDto,
      currentUser,
    );
  }

  @Patch('templates/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async updateTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Body() updateDocScheduleTemplateDto: UpdateDocScheduleTemplateDto,
    @Param('id') id: number,
  ) {
    await this.scheduleTemplatesService.update(
      updateDocScheduleTemplateDto,
      id,
      currentUser,
    );
  }

  @Delete('templates/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deleteTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('id') id: number,
  ) {
    await this.scheduleTemplatesService.delete(id, currentUser);
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

  @Delete('slots/:id')
  @HttpCode(HttpStatus.NO_CONTENT)
  async deleteSlot(@Param('id') id: number) {
    await this.scheduleSlotsService.delete(id);
  }
}
