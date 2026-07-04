import { Injectable, Logger } from '@nestjs/common';
import * as fs from 'fs';
import * as path from 'path';
import { randomUUID } from 'crypto';

@Injectable()
export class FileStorageService {
  private readonly uploadDir = path.resolve(process.cwd(), 'uploads');

  constructor() {
    fs.mkdirSync(this.uploadDir, { recursive: true });
  }

  async saveFile(
    buffer: Buffer,
    originalName: string,
    subDir: string,
  ): Promise<string> {
    const dir = path.join(this.uploadDir, subDir);
    fs.mkdirSync(dir, { recursive: true });

    const ext = path.extname(originalName) || '';
    const filename = `${randomUUID()}${ext}`;
    const filePath = path.join(dir, filename);

    await fs.promises.writeFile(filePath, buffer);

    return path.join(subDir, filename);
  }

  async deleteFile(relativePath: string): Promise<void> {
    const fullPath = path.join(this.uploadDir, relativePath);
    try {
      await fs.promises.unlink(fullPath);
    } catch (error) {
      Logger.warn(`Failed to delete file ${fullPath}: ${error}`);
    }
  }

  getFullPath(relativePath: string): string {
    return path.join(this.uploadDir, relativePath);
  }
}
