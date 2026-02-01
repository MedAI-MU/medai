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
import { DataSource } from 'typeorm';
import { DocScheduleTemplate } from '../src/schedules/entities/doc-schedule-template.entity';
import { DocScheduleSlot } from '../src/schedules/entities/doc-schedule-slot.entity';
import { DocScheduleTemplateSlot } from '../src/schedules/entities/doc-schedule-template-slot.entity';
import { Doctor } from '../src/doctors/entities/doctor.entity';
import { SchedulesModule } from '../src/schedules/schedules.module';
import { PagedListDto } from '../src/shared/dtos/paged-list.dto';
import { DocScheduleTemplateDto } from '../src/schedules/dtos/doc-schedule-template.dto';
import * as argon2 from 'argon2';

describe('SchedulesController (e2e)', () => {
  let app: INestApplication;
  let dataSource: DataSource;

  const REGISTER_USER_URL = '/api/users';
  const LOGIN_USER_URL = '/api/auth/login';
  const CREATE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const UPDATE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const DELETE_SCHEDULE_TEMPLATE_URL = '/api/doctors/schedules/templates';
  const CREATE_SCHEDULE_SLOTS_URL = '/api/doctors/schedules';
  const UPDATE_SCHEDULE_SLOT_URL = '/api/doctors/schedules/slots';
  const DELETE_SCHEDULE_SLOT_URL = '/api/doctors/schedules/slots';

  let authCookie: string;
  let doctorUserId: number;

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
  });

  beforeEach(async () => {
    await dataSource.synchronize(true);

    // Seed a secretary user directly in the database, we don't use the register API for that
    const registerDto: RegisterDto = {
      email: 'secretary@test.com',
      password: 'strongPassword123',
      name: 'Test Secretary',
      phone: '01123456789',
      role: 'secretary',
    };

    const secretary = new User({
      email: registerDto.email,
      password: await argon2.hash(registerDto.password),
      name: registerDto.name,
      phone: registerDto.phone,
      role: registerDto.role,
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
      role: 'doctor',
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
  });

  describe('/api/doctors/schedules/templates (GET)', () => {
    beforeEach(async () => {
      // Create multiple templates for testing
      const templates = [
        {
          name: 'Morning Clinic',
          doctorId: doctorUserId,
          slots: [
            { weekDay: 1, startTime: '09:00', endTime: '12:00' },
            { weekDay: 3, startTime: '09:00', endTime: '12:00' },
          ],
        },
        {
          name: 'Evening Clinic',
          doctorId: doctorUserId,
          slots: [
            { weekDay: 2, startTime: '14:00', endTime: '17:00' },
            { weekDay: 4, startTime: '14:00', endTime: '17:00' },
          ],
        },
        {
          name: 'Weekend Schedule',
          doctorId: doctorUserId,
          slots: [{ weekDay: 5, startTime: '10:00', endTime: '13:00' }],
        },
      ];

      for (const template of templates) {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_TEMPLATE_URL)
          .set('Cookie', authCookie)
          .send(template)
          .expect(201);
      }
    });

    it('should return paginated templates with default pageNo and pageSize', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
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
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
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
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?pageNo=1&pageSize=2`)
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
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?name=Morning`)
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
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?doctorId=${doctorUserId}`)
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
        .get(`${CREATE_SCHEDULE_TEMPLATE_URL}?doctorId=9999`)
        .set('Cookie', authCookie)
        .expect(200);

      const body = response.body as PagedListDto<DocScheduleTemplateDto>;
      expect(body.data).toEqual([]);
      expect(body.totalCount).toBe(0);
    });

    it('should order slots by weekDay and startTime', async () => {
      const response = await request(app.getHttpServer() as App)
        .get(CREATE_SCHEDULE_TEMPLATE_URL)
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
        .get(
          `${CREATE_SCHEDULE_TEMPLATE_URL}?name=Morning&doctorId=${doctorUserId}`,
        )
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
        doctorId: 1,
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
        doctorId: 'invalid',
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
        doctorId: 1,
        slots: [],
      },
      {
        name: 'Valid Template Name',
        doctorId: 1,
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
        doctorId: 1,
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
        doctorId: 1,
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
        doctorId: 1,
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
          .post(CREATE_SCHEDULE_TEMPLATE_URL)
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 400 when doctor does not exist', async () => {
      const dto = {
        name: 'Valid Template Name',
        doctorId: 9999,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });

    it('should create a schedule template and return 201 status', async () => {
      const dto = {
        name: 'Morning Shifts',
        doctorId: doctorUserId,
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
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 400 for overlapping time slots in the same day', async () => {
      const dto = {
        name: 'Template with overlapping slots',
        doctorId: doctorUserId,
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
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
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
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
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
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });

    it('should update template name and return 204 status', async () => {
      const updateDto = {
        name: 'Updated Template Name',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
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
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
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
        .patch(`${UPDATE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
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
        doctorId: doctorUserId,
        slots: [
          {
            weekDay: 0,
            startTime: '09:00',
            endTime: '12:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_TEMPLATE_URL)
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
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL}/9999`)
        .set('Cookie', authCookie)
        .expect(400);
    });

    it('should delete a template and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_TEMPLATE_URL}/${templateId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the template was deleted
      const deletedTemplate = await dataSource
        .getRepository(DocScheduleTemplate)
        .findOne({ where: { id: templateId } });
      expect(deletedTemplate).toBeNull();
    });
  });

  describe('/api/doctors/schedules (POST)', () => {
    const invalidCreateScheduleDtoTestCases = [
      {
        doctorId: 'invalid',
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        doctorId: 1,
        slots: [],
      },
      {
        doctorId: 1,
        slots: [
          {
            dayDate: 'invalid-date',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      },
      {
        doctorId: 1,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: 'invalid',
            endTime: '10:00',
          },
        ],
      },
      {
        doctorId: 1,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '10:00',
            endTime: '09:00',
          },
        ],
      },
      {
        doctorId: 1,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
        extraField: 'notallowed',
      },
    ];

    it.each(invalidCreateScheduleDtoTestCases)(
      'should return 400 for invalid create schedule dto: %o',
      async (invalidDto) => {
        await request(app.getHttpServer() as App)
          .post(CREATE_SCHEDULE_SLOTS_URL)
          .set('Cookie', authCookie)
          .send(invalidDto)
          .expect(400);
      },
    );

    it('should return 400 when doctor does not exist', async () => {
      const dto = {
        doctorId: 9999,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });

    it('should create schedule slots and return 201 status', async () => {
      const dto = {
        doctorId: doctorUserId,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
          {
            dayDate: '2026-02-02',
            startTime: '14:00',
            endTime: '15:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(201);
    });

    it('should return 400 for overlapping time slots on the same day', async () => {
      const dto = {
        doctorId: doctorUserId,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '12:00',
          },
          {
            dayDate: '2026-02-01',
            startTime: '11:00',
            endTime: '14:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
        .set('Cookie', authCookie)
        .send(dto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/slots/:id (PATCH)', () => {
    let slotId: number;

    beforeEach(async () => {
      // Create a schedule slot first
      const createDto = {
        doctorId: doctorUserId,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
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
    });

    it('should return 400 when slot does not exist', async () => {
      const updateDto = {
        startTime: '11:00',
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/9999`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });

    it('should update slot start time and return 204 status', async () => {
      const updateDto = {
        startTime: '08:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
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

    it('should update slot end time and return 204 status', async () => {
      const updateDto = {
        endTime: '12:00',
      };

      await request(app.getHttpServer() as App)
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
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
        .patch(`${UPDATE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .send(updateDto)
        .expect(400);
    });
  });

  describe('/api/doctors/schedules/slots/:id (DELETE)', () => {
    let slotId: number;

    beforeEach(async () => {
      // Create a schedule slot first
      const createDto = {
        doctorId: doctorUserId,
        slots: [
          {
            dayDate: '2026-02-01',
            startTime: '09:00',
            endTime: '10:00',
          },
        ],
      };

      await request(app.getHttpServer() as App)
        .post(CREATE_SCHEDULE_SLOTS_URL)
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
    });

    it('should return 400 when slot does not exist', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/9999`)
        .set('Cookie', authCookie)
        .expect(400);
    });

    it('should delete a slot and return 204 status', async () => {
      await request(app.getHttpServer() as App)
        .delete(`${DELETE_SCHEDULE_SLOT_URL}/${slotId}`)
        .set('Cookie', authCookie)
        .expect(204);

      // Verify the slot was deleted
      const deletedSlot = await dataSource
        .getRepository(DocScheduleSlot)
        .findOne({ where: { id: slotId } });
      expect(deletedSlot).toBeNull();
    });
  });

  afterAll(async () => {
    await app.close();
  });
});
