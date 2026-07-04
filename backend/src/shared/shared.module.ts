import { Module, Global } from '@nestjs/common';
import { FileStorageService } from './services/file-storage.service';

@Global()
@Module({
  providers: [FileStorageService],
  exports: [FileStorageService],
})
export class SharedModule {}
