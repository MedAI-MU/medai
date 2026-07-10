import { Test, TestingModule } from '@nestjs/testing';
import { ConfigService } from '@nestjs/config';
import { FileStorageService } from './file-storage.service';
import { BlobServiceClient } from '@azure/storage-blob';

jest.mock('@azure/storage-blob', () => {
  const mockBlockBlobClient = {
    url: 'https://storage.blob.core.windows.net/scans/uuid.jpg',
    uploadData: jest.fn().mockResolvedValue(undefined),
    deleteIfExists: jest.fn().mockResolvedValue(undefined),
  };

  const mockContainerClient = {
    createIfNotExists: jest.fn().mockResolvedValue(undefined),
    getBlockBlobClient: jest.fn().mockReturnValue(mockBlockBlobClient),
  };

  return {
    BlobServiceClient: {
      fromConnectionString: jest.fn().mockReturnValue({
        getContainerClient: jest.fn().mockReturnValue(mockContainerClient),
      }),
    },
  };
});

describe('FileStorageService', () => {
  let service: FileStorageService;
  let configService: ConfigService;

  beforeEach(async () => {
    jest.clearAllMocks();

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        FileStorageService,
        {
          provide: ConfigService,
          useValue: {
            get: jest.fn((key: string) => {
              if (key === 'AZURE_STORAGE_CONNECTION_STRING') {
                return 'DefaultEndpointsProtocol=https;AccountName=test;AccountKey=dGVzdA==;EndpointSuffix=core.windows.net';
              }
              return undefined;
            }),
          },
        },
      ],
    }).compile();

    service = module.get<FileStorageService>(FileStorageService);
    configService = module.get<ConfigService>(ConfigService);
  });

  describe('constructor', () => {
    it('should throw when connection string is missing', () => {
      (configService.get as jest.Mock).mockReturnValue(undefined);

      expect(() => new FileStorageService(configService)).toThrow(
        'AZURE_STORAGE_CONNECTION_STRING is not configured',
      );
    });

    it('should create client when connection string is present', () => {
      (configService.get as jest.Mock).mockReturnValue('conn-string');

      const svc = new FileStorageService(configService);

      expect(svc).toBeDefined();
      expect(
        (BlobServiceClient.fromConnectionString as jest.Mock).mock.calls.length,
      ).toBeGreaterThan(0);
    });
  });

  describe('saveFile', () => {
    it('should ensure container exists and upload file', async () => {
      const buffer = Buffer.from('test-data');
      const url = await service.saveFile(
        buffer,
        'image.jpg',
        'scans',
        'image/jpeg',
      );

      expect(url).toBe('https://storage.blob.core.windows.net/scans/uuid.jpg');
    });

    it('should handle files without extension', async () => {
      const buffer = Buffer.from('test');
      const url = await service.saveFile(buffer, 'noext', 'reports');

      expect(url).toBeDefined();
    });
  });

  describe('deleteFile', () => {
    it('should delete blob from given url', async () => {
      await expect(
        service.deleteFile(
          'https://storage.blob.core.windows.net/scans/uuid.jpg',
        ),
      ).resolves.not.toThrow();
    });

    it('should not throw on invalid url', async () => {
      await expect(
        service.deleteFile('not-a-valid-url'),
      ).resolves.not.toThrow();
    });

    it('should parse multi-level blob paths', async () => {
      await expect(
        service.deleteFile(
          'https://storage.blob.core.windows.net/container/folder/blob.jpg',
        ),
      ).resolves.not.toThrow();
    });
  });

  describe('getFullPath', () => {
    it('should return the given path unchanged', () => {
      expect(service.getFullPath('some/path')).toBe('some/path');
    });
  });
});
