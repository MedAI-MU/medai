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
  UseGuards,
} from '@nestjs/common';
import { DocScheduleTemplatesService } from './doc-schedule-templates.service';
import { CreateDocScheduleTemplateDto } from './dtos/create-doc-schedule-template.dto';
import { CurrentUser } from '../auth/decorators/current-user.decorator';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { ApprovedGuard } from 'src/users/guards/approved.guard';
import { VerifiedGuard } from 'src/auth/guards/verified.guard';
import { RolesGuard } from 'src/auth/guards/roles.guard';
import { SameIdGuard } from 'src/shared/guards/same-id.guard';
import type { TokenUser } from '../auth/interfaces/token-user.interface';
import { UpdateDocScheduleTemplateDto } from './dtos/update-doc-schedule-template.dto';
import { CreateDocScheduleDto } from './dtos/create-doc-schedule.dto';
import { DocScheduleSlotsService } from './doc-schedule-slots.service';
import { UpdateDocScheduleSlotDto } from './dtos/update-doc-schedule-slot.dto';
import { PagedListDto } from '../shared/dtos/paged-list.dto';
import { ApplyDocScheduleTemplateDto } from './dtos/apply-doc-schedule-template.dto';
import {
  ApiBadRequestResponse,
  ApiBody,
  ApiCreatedResponse,
  ApiNoContentResponse,
  ApiOkResponse,
  ApiParam,
  ApiQuery,
} from '@nestjs/swagger';

@Controller('doctors')
@UseGuards(ApprovedGuard, VerifiedGuard)
export class SchedulesController {
  constructor(
    private readonly scheduleTemplatesService: DocScheduleTemplatesService,
    private readonly scheduleSlotsService: DocScheduleSlotsService,
  ) {}

  @Get(':id/schedule-templates')
  @HttpCode(HttpStatus.OK)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiOkResponse({ description: 'List of schedule templates' })
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiQuery({ name: 'pageNo', required: false, type: Number })
  @ApiQuery({ name: 'pageSize', required: false, type: Number })
  @ApiQuery({ name: 'name', required: false, type: String })
  async getAllTemplates(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Query('pageNo', new ParseIntPipe({ optional: true })) pageNo = 1,
    @Query('pageSize', new ParseIntPipe({ optional: true })) pageSize = 10,
    @Query('name') name?: string,
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

  @Post(':id/schedule-templates')
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiCreatedResponse({ description: 'Schedule template created successfully' })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiBody({ type: CreateDocScheduleTemplateDto })
  async createTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Body() createDocScheduleTemplateDto: CreateDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.create(
      doctorId,
      createDocScheduleTemplateDto,
      currentUser,
    );
  }

  @Patch(':id/schedule-templates/:templateId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({
    name: 'templateId',
    description: 'Schedule template ID',
    type: Number,
  })
  @ApiNoContentResponse({
    description: 'Schedule template updated successfully',
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiBody({ type: UpdateDocScheduleTemplateDto })
  async updateTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
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

  @Delete(':id/schedule-templates/:templateId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({
    name: 'templateId',
    description: 'Schedule template ID',
    type: Number,
  })
  @ApiNoContentResponse({
    description: 'Schedule template deleted successfully',
  })
  async deleteTemplate(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Param('templateId', ParseIntPipe) templateId: number,
  ) {
    await this.scheduleTemplatesService.delete(
      templateId,
      doctorId,
      currentUser,
    );
  }

  @Get(':id/schedule-slots')
  @HttpCode(HttpStatus.OK)
  @UseGuards(RolesGuard)
  @Roles('patient', 'doctor', 'secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiOkResponse({
    description: 'Schedule slots retrieved successfully',
  })
  @ApiQuery({
    name: 'fromDate',
    type: String,
    required: false,
    description: 'Start date for filtering schedule slots',
  })
  @ApiQuery({
    name: 'toDate',
    type: String,
    required: false,
    description: 'End date for filtering schedule slots',
  })
  @ApiQuery({
    name: 'pageNo',
    type: Number,
    required: false,
    description: 'Page number for pagination',
  })
  @ApiQuery({
    name: 'pageSize',
    type: Number,
    required: false,
    description: 'Page size for pagination',
  })
  async getDoctorSlots(
    @Param('id', ParseIntPipe) doctorId: number,
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

  @Post(':id/schedule-slots')
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiBody({
    type: CreateDocScheduleDto,
    description: 'Schedule slots creation payload',
  })
  @ApiCreatedResponse({
    description: 'Schedule slots created successfully',
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  async createSlots(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Body() createDocScheduleDto: CreateDocScheduleDto,
  ) {
    await this.scheduleSlotsService.create(
      doctorId,
      createDocScheduleDto,
      currentUser,
    );
  }

  @Patch(':id/schedule-slots/:slotId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({ name: 'slotId', description: 'Schedule slot ID', type: Number })
  @ApiBody({
    type: UpdateDocScheduleSlotDto,
    description: 'Schedule slot update payload',
  })
  @ApiNoContentResponse({
    description: 'Schedule slot updated successfully',
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  async updateSlot(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
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

  @Delete(':id/schedule-slots/:slotId')
  @HttpCode(HttpStatus.NO_CONTENT)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({ name: 'slotId', description: 'Schedule slot ID', type: Number })
  @ApiNoContentResponse({
    description: 'Schedule slot deleted successfully',
  })
  async deleteSlot(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Param('slotId', ParseIntPipe) slotId: number,
  ) {
    await this.scheduleSlotsService.delete(slotId, doctorId, currentUser);
  }

  @Post(':id/schedule-templates/:templateId/apply')
  @HttpCode(HttpStatus.CREATED)
  @UseGuards(SameIdGuard)
  @Roles('secretary')
  @ApiParam({ name: 'id', description: 'Doctor ID', type: Number })
  @ApiParam({
    name: 'templateId',
    description: 'Schedule template ID',
    type: Number,
  })
  @ApiCreatedResponse({
    description: 'Schedule template applied successfully',
  })
  @ApiBadRequestResponse({ description: 'Invalid input data' })
  @ApiBody({
    type: ApplyDocScheduleTemplateDto,
    description: 'Schedule template application payload',
  })
  async applyTemplateToDoctor(
    @CurrentUser() currentUser: TokenUser,
    @Param('id', ParseIntPipe) doctorId: number,
    @Param('templateId', ParseIntPipe) templateId: number,
    @Body() applyTemplateDto: ApplyDocScheduleTemplateDto,
  ) {
    await this.scheduleTemplatesService.applyTemplate(
      templateId,
      doctorId,
      applyTemplateDto,
      currentUser,
    );
  }
}
