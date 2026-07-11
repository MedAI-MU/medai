package org.example.project.domain.usecase.voice_report

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.voice_report.VoiceReport
import org.example.project.domain.repository.voice_report.VoiceReportRepository

class UploadVoiceReportUseCase(private val repository: VoiceReportRepository) {
    suspend operator fun invoke(
        appointmentId: String,
        audioBytes: ByteArray,
        fileName: String
    ): Result<VoiceReport> = repository.uploadAudio(appointmentId, audioBytes, fileName)
}

class GetVoiceReportsUseCase(private val repository: VoiceReportRepository) {
    suspend operator fun invoke(appointmentId: String): Result<List<VoiceReport>> =
        repository.getReports(appointmentId)
}

class GetVoiceReportStatusUseCase(private val repository: VoiceReportRepository) {
    suspend operator fun invoke(appointmentId: String, reportId: String): Result<VoiceReport> =
        repository.getReportStatus(appointmentId, reportId)
}

class DeleteVoiceReportUseCase(private val repository: VoiceReportRepository) {
    suspend operator fun invoke(appointmentId: String, reportId: String): Result<Unit> =
        repository.deleteReport(appointmentId, reportId)
}

class PollVoiceReportStatusUseCase(private val repository: VoiceReportRepository) {
    operator fun invoke(appointmentId: String, reportId: String): Flow<Result<VoiceReport>> =
        repository.pollReportStatus(appointmentId, reportId)
}
