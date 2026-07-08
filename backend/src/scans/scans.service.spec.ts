import { Test, TestingModule } from '@nestjs/testing';
import { ForbiddenException, NotFoundException } from '@nestjs/common';
import { getRepositoryToken } from '@nestjs/typeorm';
import { ScansService } from './scans.service';
import { Scan } from './entities/scan.entity';
import { ScanImage } from './entities/scan-image.entity';
import { Report } from './entities/report.entity';
import { FileStorageService } from '../shared/services/file-storage.service';
import { Appointment } from '../appointments/entities/appointment.entity';
import type { TokenUser } from '../auth/interfaces/token-user.interface';

describe('ScansService', () => {
  let service: ScansService;

  const scansRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    remove: jest.fn(),
  };

  const scanImagesRepositoryMock = {
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
  };

  const reportsRepositoryMock = {
    find: jest.fn(),
    findOne: jest.fn(),
    create: jest.fn(),
    save: jest.fn(),
    remove: jest.fn(),
  };

  const appointmentsRepositoryMock = {
    find: jest.fn(),
  };

  const fileStorageMock = {
    saveFile: jest.fn(),
    deleteFile: jest.fn(),
    getFullPath: jest.fn(),
  };

  const secretaryUser: TokenUser = {
    id: 1,
    email: 'sec@test.com',
    role: 'secretary',
    status: 'approved',
  };

  const doctorUser: TokenUser = {
    id: 10,
    email: 'doc@test.com',
    role: 'doctor',
    status: 'approved',
  };

  const patientUser: TokenUser = {
    id: 5,
    email: 'pat@test.com',
    role: 'patient',
    status: 'approved',
  };

  const mockScan: Partial<Scan> = {
    id: 1,
    patientUserId: 5,
    appointmentId: 100,
    images: [
      {
        id: 1,
        scanId: 1,
        path: 'https://storage.blob.core.windows.net/scans/uuid.jpg',
      } as ScanImage,
    ],
    createdAt: new Date(),
  };

  const mockReport: Partial<Report> = {
    id: 1,
    scanId: 1,
    patientUserId: 5,
    path: 'https://storage.blob.core.windows.net/reports/uuid.pdf',
    createdAt: new Date(),
  };

  const mockImage: Partial<ScanImage> = {
    id: 1,
    scanId: 1,
    path: 'https://storage.blob.core.windows.net/scans/uuid.jpg',
    scan: mockScan as Scan,
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        ScansService,
        { provide: getRepositoryToken(Scan), useValue: scansRepositoryMock },
        {
          provide: getRepositoryToken(ScanImage),
          useValue: scanImagesRepositoryMock,
        },
        {
          provide: getRepositoryToken(Report),
          useValue: reportsRepositoryMock,
        },
        {
          provide: getRepositoryToken(Appointment),
          useValue: appointmentsRepositoryMock,
        },
        { provide: FileStorageService, useValue: fileStorageMock },
      ],
    }).compile();

    service = module.get<ScansService>(ScansService);
    jest.clearAllMocks();
  });

  it('should be defined', () => {
    expect(service).toBeDefined();
  });

  describe('findAll', () => {
    it('should return all scans for a secretary', async () => {
      scansRepositoryMock.find.mockResolvedValue([mockScan]);
      const result = await service.findAll(secretaryUser);
      expect(result).toEqual([mockScan]);
      expect(scansRepositoryMock.find).toHaveBeenCalledWith({
        relations: { images: true },
        order: { createdAt: 'DESC' },
      });
    });

    it('should return scans for doctor related patients', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([{ patientUserId: 5 }]);
      scansRepositoryMock.find.mockResolvedValue([mockScan]);
      const result = await service.findAll(doctorUser);
      expect(result).toEqual([mockScan]);
      expect(scansRepositoryMock.find).toHaveBeenCalledWith(
        expect.objectContaining({
          relations: { images: true },
          order: { createdAt: 'DESC' },
        }),
      );
    });

    it('should return empty array when doctor has no patients', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([]);
      const result = await service.findAll(doctorUser);
      expect(result).toEqual([]);
      expect(scansRepositoryMock.find).not.toHaveBeenCalled();
    });
  });

  describe('findByPatient', () => {
    it('should return scans for a patient', async () => {
      scansRepositoryMock.find.mockResolvedValue([mockScan]);
      const result = await service.findByPatient(5, patientUser);
      expect(result).toEqual([mockScan]);
      expect(scansRepositoryMock.find).toHaveBeenCalledWith({
        where: { patientUserId: 5 },
        relations: { images: true },
        order: { createdAt: 'DESC' },
      });
    });

    it('should allow secretary to get any patient scans', async () => {
      scansRepositoryMock.find.mockResolvedValue([mockScan]);
      const result = await service.findByPatient(5, secretaryUser);
      expect(result).toEqual([mockScan]);
    });

    it('should allow doctor with relation to get patient scans', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([{ patientUserId: 5 }]);
      scansRepositoryMock.find.mockResolvedValue([mockScan]);
      const result = await service.findByPatient(5, doctorUser);
      expect(result).toEqual([mockScan]);
    });

    it('should throw ForbiddenException for doctor with no relation', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([
        { patientUserId: 99 },
      ]);
      await expect(service.findByPatient(5, doctorUser)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('findOne', () => {
    it('should return scan if found', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      const result = await service.findOne(1, secretaryUser);
      expect(result).toEqual(mockScan);
    });

    it('should throw NotFoundException when scan not found', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.findOne(1, secretaryUser)).rejects.toThrow(
        NotFoundException,
      );
    });

    it('should throw ForbiddenException for unrelated doctor', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      appointmentsRepositoryMock.find.mockResolvedValue([
        { patientUserId: 99 },
      ]);
      await expect(service.findOne(1, doctorUser)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('create', () => {
    const file = {
      buffer: Buffer.from('data'),
      originalname: 'x.jpg',
    } as Express.Multer.File;

    it('should create a scan with images', async () => {
      scansRepositoryMock.create.mockReturnValue({ id: 1, patientUserId: 5 });
      scansRepositoryMock.save.mockResolvedValue({ id: 1, patientUserId: 5 });
      fileStorageMock.saveFile.mockResolvedValue(
        'https://storage.blob.core.windows.net/scans/uuid.jpg',
      );
      scanImagesRepositoryMock.create.mockReturnValue({
        scanId: 1,
        path: 'https://storage.blob.core.windows.net/scans/uuid.jpg',
      });
      scanImagesRepositoryMock.save.mockResolvedValue([
        {
          scanId: 1,
          path: 'https://storage.blob.core.windows.net/scans/uuid.jpg',
        },
      ]);

      const result = await service.create(5, 100, [file]);
      expect(result.id).toBe(1);
      expect(result.patientUserId).toBe(5);
      expect(result.images).toHaveLength(1);
      expect(fileStorageMock.saveFile).toHaveBeenCalledWith(
        file.buffer,
        file.originalname,
        'scans',
      );
    });

    it('should create scan with null appointmentId', async () => {
      scansRepositoryMock.create.mockReturnValue({ id: 2, patientUserId: 5 });
      scansRepositoryMock.save.mockResolvedValue({ id: 2, patientUserId: 5 });
      scanImagesRepositoryMock.save.mockResolvedValue([]);

      await service.create(5, null, []);
      expect(scansRepositoryMock.create).toHaveBeenCalledWith({
        patientUserId: 5,
        appointmentId: null,
      });
    });
  });

  describe('delete', () => {
    it('should delete scan and its images from storage', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      scansRepositoryMock.remove.mockResolvedValue(undefined);

      await service.delete(1);
      expect(fileStorageMock.deleteFile).toHaveBeenCalledWith(
        'https://storage.blob.core.windows.net/scans/uuid.jpg',
      );
      expect(scansRepositoryMock.remove).toHaveBeenCalledWith(mockScan);
    });

    it('should throw NotFoundException when scan not found', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.delete(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('createReport', () => {
    const file = {
      buffer: Buffer.from('data'),
      originalname: 'rpt.pdf',
    } as Express.Multer.File;

    it('should create a report', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      fileStorageMock.saveFile.mockResolvedValue(
        'https://storage.blob.core.windows.net/reports/uuid.pdf',
      );
      reportsRepositoryMock.create.mockReturnValue(mockReport);
      reportsRepositoryMock.save.mockResolvedValue(mockReport);

      const result = await service.createReport(1, 5, file);
      expect(result).toEqual(mockReport);
    });

    it('should throw NotFoundException when scan not found', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.createReport(999, 5, file)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('findReports', () => {
    it('should return reports for a scan', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      reportsRepositoryMock.find.mockResolvedValue([mockReport]);
      const result = await service.findReports(1, secretaryUser);
      expect(result).toEqual([mockReport]);
    });

    it('should throw NotFoundException when scan not found', async () => {
      scansRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.findReports(999, secretaryUser)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('findReportById', () => {
    it('should return report if found', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(mockReport);
      const result = await service.findReportById(1, secretaryUser);
      expect(result).toEqual(mockReport);
    });

    it('should throw NotFoundException when report not found', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.findReportById(999, secretaryUser)).rejects.toThrow(
        NotFoundException,
      );
    });

    it('should throw ForbiddenException for unrelated doctor', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(mockReport);
      appointmentsRepositoryMock.find.mockResolvedValue([
        { patientUserId: 99 },
      ]);
      await expect(service.findReportById(1, doctorUser)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });

  describe('deleteReport', () => {
    it('should delete report and file', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(mockReport);
      reportsRepositoryMock.remove.mockResolvedValue(undefined);

      await service.deleteReport(1);
      expect(fileStorageMock.deleteFile).toHaveBeenCalledWith(
        'https://storage.blob.core.windows.net/reports/uuid.pdf',
      );
      expect(reportsRepositoryMock.remove).toHaveBeenCalledWith(mockReport);
    });

    it('should throw NotFoundException when report not found', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.deleteReport(999)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('getImageFile', () => {
    it('should return image path and name', async () => {
      scanImagesRepositoryMock.findOne.mockResolvedValue(mockImage);
      scansRepositoryMock.findOne.mockResolvedValue(mockScan);
      fileStorageMock.getFullPath.mockReturnValue(
        'https://storage.blob.core.windows.net/scans/uuid.jpg',
      );

      const result = await service.getImageFile(1, secretaryUser);
      expect(result.path).toBe(
        'https://storage.blob.core.windows.net/scans/uuid.jpg',
      );
      expect(result.originalName).toBe('uuid.jpg');
    });

    it('should throw NotFoundException when image not found', async () => {
      scanImagesRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.getImageFile(999, secretaryUser)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('getReportFile', () => {
    it('should return report path and name', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(mockReport);
      fileStorageMock.getFullPath.mockReturnValue(
        'https://storage.blob.core.windows.net/reports/uuid.pdf',
      );

      const result = await service.getReportFile(1, secretaryUser);
      expect(result.path).toBe(
        'https://storage.blob.core.windows.net/reports/uuid.pdf',
      );
      expect(result.originalName).toBe('uuid.pdf');
    });

    it('should throw NotFoundException when report not found', async () => {
      reportsRepositoryMock.findOne.mockResolvedValue(null);
      await expect(service.getReportFile(999, secretaryUser)).rejects.toThrow(
        NotFoundException,
      );
    });
  });

  describe('findReportsByPatient', () => {
    it('should return reports for a patient', async () => {
      reportsRepositoryMock.find.mockResolvedValue([mockReport]);
      const result = await service.findReportsByPatient(5, secretaryUser);
      expect(result).toEqual([mockReport]);
    });

    it('should throw ForbiddenException for unrelated doctor', async () => {
      appointmentsRepositoryMock.find.mockResolvedValue([
        { patientUserId: 99 },
      ]);
      await expect(service.findReportsByPatient(5, doctorUser)).rejects.toThrow(
        ForbiddenException,
      );
    });
  });
});
