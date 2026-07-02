import request from 'supertest';
import { Test } from '@nestjs/testing';
import { INestApplication, ValidationPipe } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { User } from 'src/users/entities/user.entity';
import { UsersModule } from 'src/users/users.module';
import { RefreshToken } from 'src/users/entities/refresh-token.entity';
import { RegisterDto } from 'src/users/dtos/register.dto';
import { AuthModule } from 'src/auth/auth.module';
import cookieParser from 'cookie-parser';
import { ConfigModule } from '@nestjs/config';
import jwtConfig from 'src/auth/jwt.config';
import { App } from 'supertest/types';
import { DataSource, In } from 'typeorm';
import { DocScheduleTemplate } from '../src/schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from '../src/schedules/entities/doc-schedule-slot.entity';
import { DocScheduleTemplateSlot } from '../src/schedules/entities/doc-schedule-template-slot.entity';
import { DocSchedule } from '../src/schedules/entities/doc-schedule.entity';
import { Doctor } from '../src/doctors/entities/doctor.entity';
import { SchedulesModule } from '../src/schedules/schedules.module';
import { PagedListDto } from '../src/shared/dtos/paged-list.dto';
import { DocScheduleTemplateDto } from '../src/schedules/dtos/doc-schedule-template.dto';
import { ApplyDocScheduleTemplateDto } from '../src/schedules/dtos/apply-doc-schedule-template.dto';
import { CreateDocScheduleDto } from '../src/schedules/dtos/create-doc-schedule.dto';
import {
  DocScheduleDto,
  DocScheduleDayDto,
} from '../src/schedules/dtos/doc-schedule.dto';
import * as argon2 from 'argon2';

describe('SchedulesController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

  const REGISTER_USER_URL = '/api/users';
  const LOGIN_USER_URL = '/api/auth/login';
  const CREATE_SCHEDULE_TEMPLATE_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-templates`;
  const UPDATE_SCHEDULE_TEMPLATE_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-templates`;
  const DELETE_SCHEDULE_TEMPLATE_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-templates`;
  const APPLY_SCHEDULE_TEMPLATE_URL = (doctorId: number, templateId: number) =>
    `/api/doctors/${doctorId}/schedule-templates/${templateId}/apply`;
  const GET_ALL_TEMPLATES_URL = '/api/doctors/schedule-templates';
  const CREATE_SCHEDULE_SLOTS_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-slots`;
  const GET_DOCTOR_SCHEDULES_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-slots`;
  const UPDATE_SCHEDULE_SLOT_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-slots`;
  const DELETE_SCHEDULE_SLOT_URL = (doctorId: number) =>
    `/api/doctors/${doctorId}/schedule-slots`;

  let authCookie: string;
  let doctorUserId: number;
  let doctorAuthCookie: string;
  let anotherDoctorUserId: number;
  let anotherDoctorAuthCookie: string;

  beforeAll(async () => {
    const moduleRef = await Test.createTestingModule({
      imports: [
        TypeOrmModule.forRoot({
          type: 'sqljs',
          autoSave: false,
          location: ':memory:',
          synchronize: true,
          dropSchema: true,
          entities: [
            User,
            RefreshToken,
            Doctor,
            DocSchedule,
            DocScheduleSlot,
            DocScheduleTemplate,
            DocScheduleTemplateSlot,
          ],
        }),
        ConfigModule.forRoot({ isGlobal: true, load: [jwtConfig] }),
        UsersModule,
        AuthModule,
        SchedulesModule,
      ],
    }).compile();

    app = moduleRef.createNestApplication();
    app.setGlobalPrefix('api');
    app.useGlobalPipes(
      new ValidationPipe({
        whitelist: true,
        transform: true,
        forbidNonWhitelisted: true,
      }),
    );
    app.use(cookieParser());

    await app.init();

    dataSource = moduleRef.get(DataSource);

    // Create users once for all tests
    // Seed a secretary user directly in the database
    const registerDto: Pick<RegisterDto, 'email' | 'password' | 'name' | 'phone'> = {
      email: 'secretary@test.com',
      password: 'strongPassword123',
      name: 'Test Secretary',
      phone: '01123456789',
    };

    const secretary = new User({
      email: registerDto.email,
      password: await argon2.hash(registerDto.password),
      name: registerDto.name,
      phone: registerDto.phone,
      role: 'secretary',
    });
    await dataSource.getRepository(User).save(secretary);

    const loginDto = {
      email: registerDto.email,
      password: registerDto.password,
    };

    const loginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(loginDto)
      .expect(200);

    const setCookies = loginResponse.headers['set-cookie'] as string | string[];
    const cookiesArray = Array.isArray(setCookies) ? setCookies : [setCookies];
    const foundAuthCookie = cookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundAuthCookie).toBeDefined();
    authCookie = foundAuthCookie as string;

    // Register a doctor user and get their ID
    const doctorRegisterDto: RegisterDto = {
      email: 'doctor@test.com',
      password: 'strongPassword123',
      name: 'Test Doctor',
      phone: '01198765432',
    };

    await request(app.getHttpServer() as App)
      .post(REGISTER_USER_URL)
      .send(doctorRegisterDto)
      .expect(201);

    // Get the doctor user from the database
    const doctorUser = await dataSource
      .getRepository(User)
      .findOne({ where: { email: 'doctor@test.com' } });
    expect(doctorUser).toBeDefined();
    doctorUserId = doctorUser!.id;

    // Manually create a Doctor entity for the doctor user
    const doctor = new Doctor({
      userId: doctorUserId,
      specialty: 'General Practitioner',
    });
    await dataSource.getRepository(Doctor).save(doctor);

    // Login as doctor to get their auth cookie
    const doctorLoginDto = {
      email: 'doctor@test.com',
      password: 'strongPassword123',
    };

    const doctorLoginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(doctorLoginDto)
      .expect(200);

    const doctorSetCookies = doctorLoginResponse.headers['set-cookie'] as
      | string
      | string[];
    const doctorCookiesArray = Array.isArray(doctorSetCookies)
      ? doctorSetCookies
      : [doctorSetCookies];
    const foundDoctorAuthCookie = doctorCookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundDoctorAuthCookie).toBeDefined();
    doctorAuthCookie = foundDoctorAuthCookie as string;

    // Register another doctor user
    const anotherDoctorRegisterDto: RegisterDto = {
      email: 'doctor2@test.com',
      password: 'strongPassword123',
      name: 'Test Doctor 2',
      phone: '01187654321',
    };

    await request(app.getHttpServer() as App)
      .post(REGISTER_USER_URL)
      .send(anotherDoctorRegisterDto)
      .expect(201);

    // Get the second doctor user from the database
    const anotherDoctorUser = await dataSource
      .getRepository(User)
      .findOne({ where: { email: 'doctor2@test.com' } });
    expect(anotherDoctorUser).toBeDefined();
    anotherDoctorUserId = anotherDoctorUser!.id;

    // Manually create a Doctor entity for the second doctor user
    const anotherDoctor = new Doctor({
      userId: anotherDoctorUserId,
      specialty: 'Cardiologist',
    });
    await dataSource.getRepository(Doctor).save(anotherDoctor);

    // Login as second doctor to get their auth cookie
    const anotherDoctorLoginDto = {
      email: 'doctor2@test.com',
      password: 'strongPassword123',
    };

    const anotherDoctorLoginResponse = await request(app.getHttpServer() as App)
      .post(LOGIN_USER_URL)
      .send(anotherDoctorLoginDto)
      .expect(200);

    const anotherDoctorSetCookies = anotherDoctorLoginResponse.headers[
      'set-cookie'
    ] as string | string[];
    const anotherDoctorCookiesArray = Array.isArray(anotherDoctorSetCookies)
      ? anotherDoctorSetCookies
      : [anotherDoctorSetCookies];
    const foundAnotherDoctorAuthCookie = anotherDoctorCookiesArray.find((c) =>
      c.startsWith('Authentication='),
    );
    expect(foundAnotherDoctorAuthCookie).toBeDefined();
    anotherDoctorAuthCookie = foundAnotherDoctorAuthCookie as string;
  });

  beforeEach(async () => {
    // Only clear the data that changes between tests instead of full synchronize
    await dataSource.getRepository(DocScheduleSlot).clear();
    await dataSource.getRepository(DocSchedule).clear();
    await dataSource.getRepository(DocScheduleTemplateSlot).clear();
    await dataSource.getRepository(DocScheduleTemplate).clear();
  });

  describe('/api/doctors/schedules/templates (GET)', () => {
    beforeEach(async () => {
      // Create multiple templates for testing
      const templates = [
        {
          name: 'Morning Clinic',
          slots: [
            { weekDay: 1, startTime: '09:00', endTime: '12:00' },
            { weekDay: 3, startTime: '09:00', endTime: '12:00' },
          ],
        },
        {
          name: 'Evening Clinic',
          slots: [
            { weekDay: 2, startTime: '14:00', endTime: '17:00' },
            { weekDay: 4, startTime: '14:00', endTime: '17:00' },
          ],
        },
        {
          name: 'Weekend Schedule',
          slots: [{ weekDay: 5, startTime: '10:00', endTime: '13:00' }],
        },
      ];

      for (const template of templates) {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
          .set('Cookie', authCookie)
          .send(template)
          .expect(201);
      }
    });

    it('should return paginated templates with default pageNo and pageSize', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_ALL_TEMPLATES_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body).toHaveProperty('data');
      expect(body).toHaveProperty('totalCount');
      expect(body).toHaveProperty('currentPage', 1);
      expect(body).toHaveProperty('pageSize', 10);
      expect(body).toHaveProperty('hasNext');
      expect(body).toHaveProperty('hasPrevious');
      expect(Array.isArray(body.data)).toBe(true);
      expect(body.totalCount).toBeGreaterThanOrEqual(3);
    });

    it('should return templates with correct structure', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_ALL_TEMPLATES_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      const template = body.data[0];
      expect(template).toHaveProperty('id');
      expect(template).toHaveProperty('name');
      expect(template).toHaveProperty('doctor');
      expect(template.doctor).toHaveProperty('id');
      expect(template.doctor).toHaveProperty('name');
      expect(template.doctor).toHaveProperty('specialty');
      expect(template).toHaveProperty('slots');
      expect(Array.isArray(template.slots)).toBe(true);
      expect(template).toHaveProperty('createdBy');
      expect(template.createdBy).toHaveProperty('id');
      expect(template.createdBy).toHaveProperty('name');
      expect(template).toHaveProperty('createdAt');
      expect(template).toHaveProperty('updatedAt');
    });

    it('should apply custom pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_ALL_TEMPLATES_URL}?pageNo=1&pageSize=2`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.currentPage).toBe(1);
      expect(body.pageSize).toBe(2);
      expect(body.data.length).toBeLessThanOrEqual(2);
    });

    // Skipped: Requires PostgreSQL pg_trgm extension (similarity function)
    // TODO: Test with testcontainers using actual PostgreSQL
    it.skip('should filter templates by name', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_ALL_TEMPLATES_URL}?name=Morning`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data.length).toBeGreaterThan(0);
      const hasMatchingName = body.data.some((template) =>
        template.name.toLowerCase().includes('morning'),
      );
      expect(hasMatchingName).toBe(true);
    });

    it('should filter templates by doctorId', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_ALL_TEMPLATES_URL}?doctorId=${doctorUserId}`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data.length).toBeGreaterThan(0);
      body.data.forEach((template) => {
        expect(template.doctor.id).toBe(doctorUserId);
      });
    });

    it('should return empty data for non-existent doctor', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_ALL_TEMPLATES_URL}?doctorId=9999`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data).toEqual([]);
      expect(body.totalCount).toBe(0);
    });

    it('should order slots by weekDay and startTime', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_ALL_TEMPLATES_URL)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      const templateWithMultipleSlots = body.data.find(
        (t) => t.slots.length > 1,
      );

      if (templateWithMultipleSlots) {
        const slots = templateWithMultipleSlots.slots;
        for (let i = 1; i < slots.length; i++) {
          const prevSlot = slots[i - 1];
          const currentSlot = slots[i];
          expect(prevSlot.weekDay).toBeLessThanOrEqual(currentSlot.weekDay);
        }
      }
    });

    // Skipped: Requires PostgreSQL pg_trgm extension (similarity function)
    // TODO: Test with testcontainers using actual PostgreSQL
    it.skip('should combine name and doctorId filters', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(`${GET_ALL_TEMPLATES_URL}?name=Morning&doctorId=${doctorUserId}`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      body.data.forEach((template) => {
        expect(template.doctor.id).toBe(doctorUserId);
      });
    });
  });

  describe('/api/doctors/schedules/templates (POST)', () => {
    const invalidCreateTemplateDtoTestCases = [
      {
        name: 'T',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
        extraField: 'invalid',
      },
      {
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        slots: [],
      },
      {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 7,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 0,
            startTime: 'invalid',
            endTime: '10:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: 'invalid',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 0,
            startTime: '10:00',
            endTime: '09:00',
          },
        ],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
        extraField: 'notallowed',
      },
    ];

    it.each(invalidCreateTemplateDtoTestCases)(
      'should return 400 for invalid create template dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 400 when doctor does not exist', async () => {
      const dto = {
        name: 'Valid Template Name',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(9999))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(404);
    });

    it('should create a schedule template and return 201 status', async () => {
      const dto = {
        name: 'Morning Shifts',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 1,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 400 for overlapping time slots in the same day', async () => {
      const dto = {
        name: 'Template with overlapping slots',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 0,
            startTime: '11:00',
            endTime: '14:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/templates/:id (PATCH)', () => {
    let templateId: number;

    beforeEach(async () => {
      // Create a template first
      const createDto = {
        name: 'Original Template',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created template ID from the database
      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { name: 'Original Template' } });
      expect(template).toBeDefined();
      templateId = template!.id;
    });

    it('should return 400 when template does not exist', async () => {
      const updateDto = {
        name: 'Updated Template Name',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(404);
    });

    it('should update template name and return 204 status', async () => {
      const updateDto = {
        name: 'Updated Template Name',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { id: templateId } });
      expect(updatedTemplate).toBeDefined();
      expect(updatedTemplate!.name).toBe('Updated Template Name');
    });

    it('should update template slots and return 204 status', async () => {
      const updateDto = {
        slots: [
          {
            weekDay: 1,
            startTime: '14:00',
            endTime: '17:00',
          },
          {
            weekDay: 2,
            startTime: '14:00',
            endTime: '17:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the slots were updated
      const updatedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { id: templateId },
          relations: ['slots'],
        });
      expect(updatedTemplate).toBeDefined();
      expect(updatedTemplate!.slots).toHaveLength(2);
    });

    it('should return 400 for overlapping time slots in update', async () => {
      const updateDto = {
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            weekDay: 0,
            startTime: '11:00',
            endTime: '14:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/${templateId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/templates/:id (DELETE)', () => {
    let templateId: number;

    beforeEach(async () => {
      // Create a template first
      const createDto = {
        name: 'Template to delete',
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created template ID
      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { name: 'Template to delete' } });
      expect(template).toBeDefined();
      templateId = template!.id;
    });

    it('should return 400 when template does not exist', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/9999`)
        .set('Cookie', authCookie)
        .expect(404);
    });

    it('should delete a template and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL(doctorUserId)}/${templateId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the template was deleted
      const deletedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { id: templateId } });
      expect(deletedTemplate).toBeNull();
    });
  });

  describe('/api/doctors/:doctorId/schedule-templates/:templateId/apply (POST)', () => {
    let templateId: number;

    beforeEach(async () => {
      const templateDto = {
        name: `Apply Template ${Date.now()}`,
        slots: [
          { weekDay: 1, startTime: '09:00', endTime: '10:00' },
          { weekDay: 3, startTime: '11:00', endTime: '12:00' },
          { weekDay: 5, startTime: '14:00', endTime: '15:00' },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(templateDto)
        .expect(201);

      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { doctor: { userId: doctorUserId } },
          order: { createdAt: 'DESC' },
        });
      expect(template).toBeDefined();
      templateId = template!.id;
    });

    it('should apply template and create schedules/slots for matching weekdays', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-04-06',
        endDate: '2026-04-12',
      };

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(201);

      const templateSlots = await dataSource
        .getRepository(DocScheduleTemplateSlot)
        .find({ where: { template: { id: templateId } } });

      const applicableDates: string[] = [];
      const currentDate = new Date(applyDto.startDate);
      const endDate = new Date(applyDto.endDate);

      while (currentDate <= endDate) {
        const weekDay = currentDate.getDay();
        if (templateSlots.some((slot) => slot.weekDay === weekDay)) {
          applicableDates.push(currentDate.toISOString().split('T')[0]);
        }
        currentDate.setDate(currentDate.getDate() + 1);
      }

      const schedules = await dataSource.getRepository(DocSchedule).find({
        where: {
          doctor: { userId: doctorUserId },
          dayDate: In(applicableDates),
        },
        relations: ['slots'],
      });

      expect(schedules.length).toBe(applicableDates.length);
      schedules.forEach((schedule) => {
        expect(schedule.slots.length).toBeGreaterThan(0);
      });
    });

    it('should allow secretary to apply template for any doctor', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-04-13',
        endDate: '2026-04-19',
      };

      const response = await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto);

      expect(response.status).toBe(201);
    });

    it('should allow doctor to apply template for themselves', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-04-20',
        endDate: '2026-04-26',
      };

      const response = await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', doctorAuthCookie)
        .send(applyDto);

      expect(response.status).toBe(201);
    });

    it('should return 401 when doctor applies template for another doctor', async () => {
      const otherTemplateDto = {
        name: `Other Doctor Template ${Date.now()}`,
        slots: [{ weekDay: 2, startTime: '09:00', endTime: '10:00' }],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(anotherDoctorUserId))
        .set('Cookie', authCookie)
        .send(otherTemplateDto)
        .expect(201);

      const otherTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { doctor: { userId: anotherDoctorUserId } },
          order: { createdAt: 'DESC' },
        });
      expect(otherTemplate).toBeDefined();

      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-04-27',
        endDate: '2026-05-03',
      };

      await request(app.getHttpServer() as App)
        .post(
          APPLY_SCHEDULE_TEMPLATE_URL(anotherDoctorUserId, otherTemplate!.id),
        )
        .set('Cookie', doctorAuthCookie)
        .send(applyDto)
        .expect(401);
    });

    it('should return 404 when template does not exist', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-05-04',
        endDate: '2026-05-10',
      };

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, 99999))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(404);
    });

    it('should return 400 when startDate is missing', async () => {
      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send({ endDate: '2026-05-10' })
        .expect(400);
    });

    it('should return 400 when endDate is missing', async () => {
      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send({ startDate: '2026-05-04' })
        .expect(400);
    });

    it('should return 400 when startDate is after endDate', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-05-20',
        endDate: '2026-05-10',
      };

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(400);
    });

    it('should return 400 when dates have invalid format', async () => {
      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send({ startDate: 'invalid', endDate: 'also-invalid' })
        .expect(400);
    });

    it('should return 400 when overlapping slots are detected', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-05-11',
        endDate: '2026-05-17',
      };

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(201);

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(400);
    });

    it('should reuse existing schedule when adding slots to an already scheduled day', async () => {
      // First, create a schedule slot for a specific date
      const scheduleDto: CreateDocScheduleDto = {
        days: [
          {
            date: '2027-09-15',
            slots: [{ startTime: '09:00', endTime: '10:00' }],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(scheduleDto)
        .expect(201);

      const schedulesBeforeCount = await dataSource
        .getRepository(DocSchedule)
        .count({
          where: { doctor: { userId: doctorUserId }, dayDate: '2027-09-15' },
        });
      expect(schedulesBeforeCount).toBe(1);

      // Create a template that matches the day of the week for Sep 15, 2027
      // Sep 15, 2027 is a Wednesday (weekDay 3)
      const newTemplateDto = {
        name: `Wednesday Template ${Date.now()}`,
        slots: [{ weekDay: 3, startTime: '16:00', endTime: '17:00' }],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(newTemplateDto)
        .expect(201);

      const newTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { doctor: { userId: doctorUserId } },
          order: { createdAt: 'DESC' },
        });

      // Apply the new template to include the same date (Sep 15)
      // Should add a slot to the existing schedule, not create a new one
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2027-09-15',
        endDate: '2027-09-15',
      };

      await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, newTemplate!.id))
        .set('Cookie', authCookie)
        .send(applyDto)
        .expect(201);

      // Should still have only 1 schedule for that date
      const schedulesAfterCount = await dataSource
        .getRepository(DocSchedule)
        .count({
          where: { doctor: { userId: doctorUserId }, dayDate: '2027-09-15' },
        });
      expect(schedulesAfterCount).toBe(1);

      // But it should now have 2 slots
      const schedule = await dataSource.getRepository(DocSchedule).findOne({
        where: { doctor: { userId: doctorUserId }, dayDate: '2027-09-15' },
        relations: ['slots'],
      });
      expect(schedule!.slots.length).toBe(2);
    });

    it('should apply template with same startDate and endDate', async () => {
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2026-06-01',
        endDate: '2026-06-01',
      };

      const response = await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto);

      expect([200, 201]).toContain(response.status);
    });

    it('should create slots with correct times from template', async () => {
      // Verify that when a template is applied, the created slots
      // have the same time values as defined in the template slots

      // Get the template that was created in beforeEach
      const template = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({
          where: { id: templateId },
          relations: ['slots'],
        });
      expect(template).toBeDefined();
      expect(template!.slots.length).toBeGreaterThan(0);

      // Apply the template to a wide date range
      const applyDto: ApplyDocScheduleTemplateDto = {
        startDate: '2028-01-01',
        endDate: '2028-01-31',
      };

      const response = await request(app.getHttpServer() as App)
        .post(APPLY_SCHEDULE_TEMPLATE_URL(doctorUserId, templateId))
        .set('Cookie', authCookie)
        .send(applyDto);

      expect([200, 201]).toContain(response.status);

      // Get all created slots for this doctor
      const createdSlots = await dataSource
        .getRepository(DocScheduleSlot)
        .find({
          where: {
            schedule: { doctor: { userId: doctorUserId } },
          },
        });

      // Verify that any created slots have times that match the template slots
      const templateTimes = template!.slots.map((s) => ({
        startTime: s.startTime,
        endTime: s.endTime,
      }));

      // Each created slot should have times that match one of the template slots
      createdSlots.forEach((slot) => {
        const matchesTemplate = templateTimes.some(
          (t) => t.startTime === slot.startTime && t.endTime === slot.endTime,
        );
        // Only check slots that might be from our template
        // (since other tests may have created slots too)
        if (
          slot.startTime === '09:00:00' ||
          slot.startTime === '11:00:00' ||
          slot.startTime === '14:00:00'
        ) {
          expect(matchesTemplate).toBe(true);
        }
      });
    });
  });

  describe('/api/doctors/schedules (POST)', () => {
    const getInvalidCreateScheduleDtoTestCases = () => [
      {
        doctorId: 'invalid',
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        days: [],
      },
      {
        days: [
          {
            date: 'invalid-date',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: 'invalid',
                endTime: '10:00',
              },
            ],
          },
        ],
      },
      {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '10:00',
                endTime: '09:00',
              },
            ],
          },
        ],
      },
      {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
        extraField: 'notallowed',
      },
    ];

    it.each(getInvalidCreateScheduleDtoTestCases())(
      'should return 400 for invalid create schedule dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 404 when doctor does not exist', async () => {
      const dto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(9999))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(404);
    });

    it('should create schedule slots and return 201 status', async () => {
      const dto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
          {
            date: '2026-02-02',
            slots: [
              {
                startTime: '14:00',
                endTime: '15:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should create schedule slots successfully as doctor for themselves', async () => {
      const dto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-03',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', doctorAuthCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 401 when doctor tries to create schedule for another doctor', async () => {
      const dto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(anotherDoctorUserId))
        .set('Cookie', doctorAuthCookie)
        .send(dto)
        .expect(401);
    });

    it('should return 400 for overlapping time slots on the same day', async () => {
      const dto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '12:00',
              },
              {
                startTime: '11:00',
                endTime: '14:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });
  });

  describe('/api/doctors/:doctorId/schedule-slots (GET)', () => {
    beforeEach(async () => {
      // Create multiple schedule slots for the doctor
      const createDto: CreateDocScheduleDto = {
        days: [
          {
            date: '2026-02-10',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
              {
                startTime: '11:00',
                endTime: '12:00',
              },
            ],
          },
          {
            date: '2026-02-11',
            slots: [
              {
                startTime: '14:00',
                endTime: '15:00',
              },
            ],
          },
          {
            date: '2026-02-15',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);
    });

    it('should return 404 when doctor does not exist', async () => {
      await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(9999))
        .set('Cookie', authCookie)
        .expect(404);
    });

    it('should get doctor schedules with default pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body).toHaveProperty('doctorId', doctorUserId);
      expect(body).toHaveProperty('name');
      expect(body).toHaveProperty('speciality');
      expect(body).toHaveProperty('days');
      expect(body.days).toHaveProperty('data');
      expect(body.days).toHaveProperty('totalCount', 3);
      expect(body.days).toHaveProperty('currentPage', 1);
      expect(body.days).toHaveProperty('pageSize', 10);
      expect(body.days.data).toHaveLength(3);
    });

    it('should return schedules with correct structure', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const firstDay = body.days.data[0];
      expect(firstDay).toHaveProperty('day');
      expect(firstDay).toHaveProperty('slots');
      expect(Array.isArray(firstDay.slots)).toBe(true);
      expect(firstDay.slots[0]).toHaveProperty('id');
      expect(firstDay.slots[0]).toHaveProperty('startTime');
      expect(firstDay.slots[0]).toHaveProperty('endTime');
      expect(firstDay.slots[0]).toHaveProperty('status');
    });

    it('should apply custom pagination', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .query({ pageNo: 1, pageSize: 2 })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(3);
      expect(body.days.currentPage).toBe(1);
      expect(body.days.pageSize).toBe(2);
      expect(body.days.data).toHaveLength(2);
    });

    it('should filter schedules by fromDate', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .query({ fromDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[0].day).toBe('2026-02-11');
    });

    it('should filter schedules by toDate', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .query({ toDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[1].day).toBe('2026-02-11');
    });

    it('should filter schedules by date range', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .query({ fromDate: '2026-02-10', toDate: '2026-02-11' })
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(2);
      expect(body.days.data[0].day).toBe('2026-02-10');
      expect(body.days.data[1].day).toBe('2026-02-11');
    });

    it('should order schedules by date ascending', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const days = body.days.data.map((d: DocScheduleDayDto) => d.day);
      expect(days).toEqual(['2026-02-10', '2026-02-11', '2026-02-15']);
    });

    it('should order slots within a day by startTime ascending', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(doctorUserId))
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      const firstDaySlots = body.days.data[0].slots;
      expect(firstDaySlots[0].startTime).toContain('09:00');
      expect(firstDaySlots[1].startTime).toContain('11:00');
    });

    it('should return empty data for doctor with no schedules', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(GET_DOCTOR_SCHEDULES_URL(anotherDoctorUserId))
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as DocScheduleDto;
      expect(body.days.totalCount).toBe(0);
      expect(body.days.data).toHaveLength(0);
    });
  });

  describe('/api/doctors/schedules/slots/:id (PATCH)', () => {
    let slotId: number;
    let doctorOwnSlotId: number;

    beforeEach(async () => {
      // Secretary creates a schedule slot for doctorUserId
      const createDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created slot ID
      const slot = await dataSource.getRepository(DocScheduleSlot).findOne({
        where: {},
        order: { id: 'DESC' },
      });
      expect(slot).toBeDefined();
      slotId = slot!.id;

      // Doctor creates their own slot
      const doctorCreateDto = {
        days: [
          {
            date: '2026-02-05',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', doctorAuthCookie)
        .send(doctorCreateDto)
        .expect(201);

      // Get the doctor's own slot ID
      const doctorSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({
          where: {},
          order: { id: 'DESC' },
        });
      expect(doctorSlot).toBeDefined();
      doctorOwnSlotId = doctorSlot!.id;
    });

    it('should return 404 when slot does not exist', async () => {
      const updateDto = {
        startTime: '11:00',
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(404);
    });

    it('should update slot start time as secretary and return 204 status', async () => {
      const updateDto = {
        startTime: '08:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.startTime).toContain('08:00');
    });

    it('should update slot as doctor for their own slot and return 204 status', async () => {
      const updateDto = {
        startTime: '11:00',
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/${doctorOwnSlotId}`)
        .set('Cookie', doctorAuthCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: doctorOwnSlotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.startTime).toContain('11:00');
    });

    it('should return 401 when doctor tries to update another doctors slot', async () => {
      const updateDto = {
        startTime: '11:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', anotherDoctorAuthCookie)
        .send(updateDto)
        .expect(401);
    });

    it('should update slot end time and return 204 status', async () => {
      const updateDto = {
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(204);

      // Verify the update
      const updatedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(updatedSlot).toBeDefined();
      expect(updatedSlot!.endTime).toContain('12:00');
    });

    it('should return 400 when updating with invalid time range', async () => {
      const updateDto = {
        startTime: '12:00',
        endTime: '11:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/slots/:id (DELETE)', () => {
    let slotId: number;
    let doctorOwnSlotId: number;

    beforeEach(async () => {
      // Secretary creates a schedule slot
      const createDto = {
        days: [
          {
            date: '2026-02-01',
            slots: [
              {
                startTime: '09:00',
                endTime: '10:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', authCookie)
        .send(createDto)
        .expect(201);

      // Get the created slot ID
      const slot = await dataSource.getRepository(DocScheduleSlot).findOne({
        where: {},
        order: { id: 'DESC' },
      });
      expect(slot).toBeDefined();
      slotId = slot!.id;

      // Doctor creates their own slot
      const doctorCreateDto = {
        days: [
          {
            date: '2026-02-06',
            slots: [
              {
                startTime: '10:00',
                endTime: '11:00',
              },
            ],
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL(doctorUserId))
        .set('Cookie', doctorAuthCookie)
        .send(doctorCreateDto)
        .expect(201);

      // Get the doctor's own slot ID
      const doctorSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({
          where: {},
          order: { id: 'DESC' },
        });
      expect(doctorSlot).toBeDefined();
      doctorOwnSlotId = doctorSlot!.id;
    });

    it('should return 400 when slot does not exist', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL(doctorUserId)}/9999`)
        .set('Cookie', authCookie)
        .expect(400);
    });

    it('should delete a slot as secretary and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the slot was deleted
      const deletedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(deletedSlot).toBeNull();
    });

    it('should delete a slot as doctor for their own slot and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL(doctorUserId)}/${doctorOwnSlotId}`)
        .set('Cookie', doctorAuthCookie)
        .expect(204);

      // Verify the slot was deleted
      const deletedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: doctorOwnSlotId } });
      expect(deletedSlot).toBeNull();
    });

    it('should return 400 when doctor tries to delete another doctors slot', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL(doctorUserId)}/${slotId}`)
        .set('Cookie', anotherDoctorAuthCookie)
        .expect(401);
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
