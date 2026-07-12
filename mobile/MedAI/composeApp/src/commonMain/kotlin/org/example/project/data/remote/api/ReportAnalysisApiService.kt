package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.example.project.data.remote.dto.report_analysis.ReportAnalysisResponseDto

interface ReportAnalysisApiService {
    suspend fun uploadReport(
        patientUserId: Int,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        appointmentId: Int?
    ): ReportAnalysisResponseDto

    suspend fun triggerAnalysis(reportId: Int): ReportAnalysisResponseDto

    suspend fun getAnalysis(reportId: Int): ReportAnalysisResponseDto

    suspend fun findAllReports(): List<ReportAnalysisResponseDto>
}

class KtorReportAnalysisApiService(
    private val client: HttpClient
) : ReportAnalysisApiService {

    override suspend fun uploadReport(
        patientUserId: Int,
        fileBytes: ByteArray,
        fileName: String,
        mimeType: String,
        appointmentId: Int?
    ): ReportAnalysisResponseDto {
        return client.post("reports/$patientUserId") {
            headers.remove(HttpHeaders.ContentType)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", fileBytes, Headers.build {
                            append(HttpHeaders.ContentType, mimeType)
                            append(HttpHeaders.ContentDisposition, "form-data; name=\"file\"; filename=\"$fileName\"")
                        })
                        if (appointmentId != null) {
                            append("appointmentId", appointmentId.toString())
                        }
                    }
                )
            )
        }.body()
    }

    override suspend fun triggerAnalysis(reportId: Int): ReportAnalysisResponseDto {
        return client.post("report/$reportId/analyze").body()
    }

    override suspend fun getAnalysis(reportId: Int): ReportAnalysisResponseDto {
        return client.get("report/$reportId/analyze").body()
    }

    override suspend fun findAllReports(): List<ReportAnalysisResponseDto> {
        return client.get("reports").body()
    }
}
