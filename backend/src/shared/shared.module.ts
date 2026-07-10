import { Module, Global } from '@nestjs/common';
import { DoctorAssignedGuard } from './guards/doctor-assigned.guard';
import { FileStorageService } from './services/file-storage.service';

@Global()
@Module({
  providers: [FileStorageService, DoctorAssignedGuard],
  exports: [FileStorageService, DoctorAssignedGuard],
})
export class SharedModule {}
