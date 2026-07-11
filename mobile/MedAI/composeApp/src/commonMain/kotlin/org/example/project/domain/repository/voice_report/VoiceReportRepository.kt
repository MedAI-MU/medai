package org.example.project.domain.repository.voice_report

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.voice_report.VoiceReport

interface VoiceReportRepository {
    suspend fun uploadAudio(
        appointmentId: String,
        audioBytes: ByteArray,
        fileName: String
    ): Result<VoiceReport>

    suspend fun getReports(appointmentId: String): Result<List<VoiceReport>>

    suspend fun getReportStatus(appointmentId: String, reportId: String): Result<VoiceReport>

    suspend fun deleteReport(appointmentId: String, reportId: String): Result<Unit>

    fun pollReportStatus(appointmentId: String, reportId: String): Flow<Result<VoiceReport>>
}
