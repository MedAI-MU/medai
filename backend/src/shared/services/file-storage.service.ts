import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { BlobServiceClient } from '@azure/storage-blob';
import { randomUUID } from 'crypto';

@Injectable()
export class FileStorageService {
  private readonly blobServiceClient: BlobServiceClient;
  private readonly logger = new Logger(FileStorageService.name);

  constructor(configService: ConfigService) {
    const connectionString = configService.get<string>(
      'AZURE_STORAGE_CONNECTION_STRING',
    );
    if (!connectionString) {
      throw new Error('AZURE_STORAGE_CONNECTION_STRING is not configured');
    }
    this.blobServiceClient =
      BlobServiceClient.fromConnectionString(connectionString);
  }

  private getContainerClient(containerName: string) {
    return this.blobServiceClient.getContainerClient(containerName);
  }

  private async ensureContainer(containerName: string): Promise<void> {
    const containerClient = this.getContainerClient(containerName);
    await containerClient.createIfNotExists({ access: 'blob' });
  }

  async saveFile(
    buffer: Buffer,
    originalName: string,
    moduleName: string,
  ): Promise<string> {
    await this.ensureContainer(moduleName);

    const containerClient = this.getContainerClient(moduleName);
    const ext = originalName.includes('.')
      ? `.${originalName.split('.').pop()}`
      : '';
    const blobName = `${randomUUID()}${ext}`;
    const blockBlobClient = containerClient.getBlockBlobClient(blobName);

    await blockBlobClient.uploadData(buffer);

    return blockBlobClient.url;
  }

  async deleteFile(blobUrl: string): Promise<void> {
    try {
      const url = new URL(blobUrl);
      const pathParts = url.pathname.split('/').filter(Boolean);
      const containerName = pathParts[0];
      const blobName = pathParts.slice(1).join('/');

      const containerClient = this.getContainerClient(containerName);
      const blockBlobClient = containerClient.getBlockBlobClient(blobName);
      await blockBlobClient.deleteIfExists();
    } catch (error) {
      this.logger.warn(
        `Failed to delete blob ${blobUrl}: ${(error as Error).message}`,
      );
    }
  }

  getFullPath(relativePath: string): string {
    return relativePath;
  }
}
