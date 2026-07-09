import { Injectable, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { BlobServiceClient } from '@azure/storage-blob';
import { randomUUID } from 'crypto';
import { access, mkdir, unlink, writeFile } from 'fs/promises';
import { join, extname } from 'path';
import { constants } from 'fs';

@Injectable()
export class FileStorageService {
  private readonly blobServiceClient: BlobServiceClient | null = null;
  private readonly logger = new Logger(FileStorageService.name);
  private readonly useLocalDisk: boolean;
  private readonly uploadsDir = 'uploads';

  constructor(private readonly configService: ConfigService) {
    const connectionString = configService.get<string>(
      'AZURE_STORAGE_CONNECTION_STRING',
    );

    if (connectionString) {
      this.blobServiceClient =
        BlobServiceClient.fromConnectionString(connectionString);
      this.useLocalDisk = false;
    } else {
      this.useLocalDisk = true;
      this.logger.log(
        'AZURE_STORAGE_CONNECTION_STRING not configured — using local disk storage',
      );
    }
  }

  private async ensureDir(dir: string): Promise<void> {
    try {
      await access(dir, constants.F_OK);
    } catch {
      await mkdir(dir, { recursive: true });
    }
  }

  private async saveToDisk(
    buffer: Buffer,
    moduleName: string,
    fileName: string,
  ): Promise<string> {
    const dir = join(this.uploadsDir, moduleName);
    await this.ensureDir(dir);
    const filePath = join(dir, fileName);
    await writeFile(filePath, buffer);
    return `${moduleName}/${fileName}`;
  }

  private async deleteFromDisk(relativePath: string): Promise<void> {
    const fullPath = join(this.uploadsDir, relativePath);
    try {
      await unlink(fullPath);
    } catch (error) {
      this.logger.warn(
        `Failed to delete local file ${fullPath}: ${(error as Error).message}`,
      );
    }
  }

  private getBaseUrl(): string {
    return (
      this.configService.get<string>('APP_URL') ||
      `http://localhost:${this.configService.get<string>('APP_PORT') || 8000}`
    );
  }

  private getContainerClient(containerName: string) {
    return this.blobServiceClient!.getContainerClient(containerName);
  }

  private async ensureContainer(containerName: string): Promise<void> {
    const containerClient = this.getContainerClient(containerName);
    await containerClient.createIfNotExists({ access: 'blob' });
  }

  async saveFile(
    buffer: Buffer,
    originalName: string,
    moduleName: string,
    mimeType?: string,
  ): Promise<string> {
    if (this.useLocalDisk) {
      const ext = originalName.includes('.') ? extname(originalName) : '';
      const fileName = `${randomUUID()}${ext}`;
      return this.saveToDisk(buffer, moduleName, fileName);
    }

    await this.ensureContainer(moduleName);

    const containerClient = this.getContainerClient(moduleName);
    const ext = originalName.includes('.')
      ? `.${originalName.split('.').pop()}`
      : '';
    const blobName = `${randomUUID()}${ext}`;
    const blockBlobClient = containerClient.getBlockBlobClient(blobName);

    await blockBlobClient.uploadData(buffer, {
      blobHTTPHeaders: { blobContentType: mimeType },
    });

    return blockBlobClient.url;
  }

  async deleteFile(path: string): Promise<void> {
    if (this.useLocalDisk) {
      await this.deleteFromDisk(path);
      return;
    }

    try {
      const url = new URL(path);
      const pathParts = url.pathname.split('/').filter(Boolean);
      const containerName = pathParts[0];
      const blobName = pathParts.slice(1).join('/');

      const containerClient = this.getContainerClient(containerName);
      const blockBlobClient = containerClient.getBlockBlobClient(blobName);
      await blockBlobClient.deleteIfExists();
    } catch (error) {
      this.logger.warn(
        `Failed to delete blob ${path}: ${(error as Error).message}`,
      );
    }
  }

  getFullPath(relativePath: string): string {
    if (
      relativePath.startsWith('http://') ||
      relativePath.startsWith('https://')
    ) {
      return relativePath;
    }
    return `${this.getBaseUrl()}/${this.uploadsDir}/${relativePath}`;
  }
}
