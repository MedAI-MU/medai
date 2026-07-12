package org.example.project.data.repository

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.project.data.remote.api.ReportAnalysisApiService
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.report_analysis.ReportAnalysis
import org.example.project.domain.repository.report_analysis.ReportAnalysisRepository

class NetworkReportAnalysisRepository(
    private val apiService: ReportAnalysisApiService
) : ReportAnalysisRepository {

    override suspend fun uploadReport(
        patientUserId: String,
        fileBytes: ByteArray,
        fileName: String,
        appointmentId: String?
    ): Result<ReportAnalysis> = safeApiCall {
        val patientIdInt = patientUserId.toIntOrNull() ?: throw IllegalArgumentException("Invalid Patient ID: $patientUserId")
        val apptIdInt = appointmentId?.toIntOrNull()
        val mimeType = getMimeType(fileName)
        val dto = apiService.uploadReport(patientIdInt, fileBytes, fileName, mimeType, apptIdInt)
        dto.toDomain()
    }

    override suspend fun triggerAnalysis(reportId: String): Result<ReportAnalysis> = safeApiCall {
        val idInt = reportId.toIntOrNull() ?: throw IllegalArgumentException("Invalid Report ID: $reportId")
        val dto = apiService.triggerAnalysis(idInt)
        dto.toDomain()
    }

    override suspend fun getAnalysis(reportId: String): Result<ReportAnalysis> = safeApiCall {
        val idInt = reportId.toIntOrNull() ?: throw IllegalArgumentException("Invalid Report ID: $reportId")
        val dto = apiService.getAnalysis(idInt)
        dto.toDomain()
    }

    override suspend fun getAllReports(): Result<List<ReportAnalysis>> = safeApiCall {
        val dtos = apiService.findAllReports()
        dtos.map { it.toDomain() }
    }

    override fun pollAnalysisStatus(reportId: String): Flow<Result<ReportAnalysis>> = flow {
        while (true) {
            val result = getAnalysis(reportId)
            emit(result)

            val report = result.getOrNull()
            if (report == null || report.analysisStatus?.isTerminal() == true) {
                break
            }
            delay(3000)
        }
    }

    private fun getMimeType(fileName: String): String {
        val ext = fileName.substringAfterLast(".", "").lowercase()
        return when (ext) {
            "pdf" -> "application/pdf"
            "png" -> "image/png"
            "webp" -> "image/webp"
            else -> "image/jpeg"
        }
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
