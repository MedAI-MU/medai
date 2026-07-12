package org.example.project.domain.repository.report_analysis

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.report_analysis.ReportAnalysis

interface ReportAnalysisRepository {
    suspend fun uploadReport(
        patientUserId: String,
        fileBytes: ByteArray,
        fileName: String,
        appointmentId: String?
    ): Result<ReportAnalysis>

    suspend fun triggerAnalysis(reportId: String): Result<ReportAnalysis>

    suspend fun getAnalysis(reportId: String): Result<ReportAnalysis>

    suspend fun getAllReports(): Result<List<ReportAnalysis>>

    fun pollAnalysisStatus(reportId: String): Flow<Result<ReportAnalysis>>
}
