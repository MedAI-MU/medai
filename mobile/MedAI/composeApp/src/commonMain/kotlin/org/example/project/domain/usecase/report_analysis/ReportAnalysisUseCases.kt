package org.example.project.domain.usecase.report_analysis

import kotlinx.coroutines.flow.Flow
import org.example.project.domain.model.report_analysis.ReportAnalysis
import org.example.project.domain.repository.report_analysis.ReportAnalysisRepository

class UploadReportUseCase(
    private val repository: ReportAnalysisRepository
) {
    suspend operator fun invoke(
        patientUserId: String,
        fileBytes: ByteArray,
        fileName: String,
        appointmentId: String?
    ): Result<ReportAnalysis> {
        if (fileBytes.size > 10 * 1024 * 1024) {
            return Result.failure(IllegalArgumentException("File size must not exceed 10MB"))
        }
        val ext = fileName.substringAfterLast(".", "").lowercase()
        if (ext !in listOf("pdf", "jpg", "jpeg", "png", "webp")) {
            return Result.failure(IllegalArgumentException("Only PDF, JPEG, PNG, and WebP files are supported"))
        }
        return repository.uploadReport(patientUserId, fileBytes, fileName, appointmentId)
    }
}

class TriggerAnalysisUseCase(
    private val repository: ReportAnalysisRepository
) {
    suspend operator fun invoke(reportId: String): Result<ReportAnalysis> {
        return repository.triggerAnalysis(reportId)
    }
}

class PollAnalysisStatusUseCase(
    private val repository: ReportAnalysisRepository
) {
    operator fun invoke(reportId: String): Flow<Result<ReportAnalysis>> {
        return repository.pollAnalysisStatus(reportId)
    }
}

class GetAllReportsUseCase(
    private val repository: ReportAnalysisRepository
) {
    suspend operator fun invoke(): Result<List<ReportAnalysis>> {
        return repository.getAllReports()
    }
}

class GetAnalysisUseCase(
    private val repository: ReportAnalysisRepository
) {
    suspend operator fun invoke(reportId: String): Result<ReportAnalysis> {
        return repository.getAnalysis(reportId)
    }
}
