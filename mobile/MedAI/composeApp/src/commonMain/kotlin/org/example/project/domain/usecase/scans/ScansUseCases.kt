package org.example.project.domain.usecase.scans

import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.Report
import org.example.project.domain.repository.scans.ScansRepository

class GetScansUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(): Result<List<Scan>> = repository.getScans()
}

class GetScanDetailsUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, scanId: String): Result<Scan> =
        repository.getScanDetails(patientUserId, scanId)
}

class UploadScanUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(
        patientUserId: String,
        appointmentId: String?,
        images: List<ByteArray>
    ): Result<Scan> = repository.uploadScan(patientUserId, appointmentId, images)
}

class DeleteScanUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, scanId: String): Result<Unit> =
        repository.deleteScan(patientUserId, scanId)
}

class GetScanReportsUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, scanId: String): Result<List<Report>> =
        repository.getScanReports(patientUserId, scanId)
}

class UploadReportUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(
        patientUserId: String,
        scanId: String,
        fileBytes: ByteArray,
        fileName: String
    ): Result<Report> = repository.uploadReport(patientUserId, scanId, fileBytes, fileName)
}

class DeleteReportUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, reportId: String): Result<Unit> =
        repository.deleteReport(patientUserId, reportId)
}

class GetScanImageFileUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, imageId: String): Result<ByteArray> =
        repository.getScanImageFile(patientUserId, imageId)
}

class GetReportFileUseCase(private val repository: ScansRepository) {
    suspend operator fun invoke(patientUserId: String, reportId: String): Result<ByteArray> =
        repository.getReportFile(patientUserId, reportId)
}
