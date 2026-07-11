package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.delete
import io.ktor.client.request.setBody
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.example.project.data.remote.dto.voice_report.VoiceReportResponseDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.voice_report.VoiceReport
import org.example.project.domain.repository.voice_report.VoiceReportRepository

class NetworkVoiceReportRepository(
    private val client: HttpClient
) : VoiceReportRepository {

    override suspend fun uploadAudio(
        appointmentId: String,
        audioBytes: ByteArray,
        fileName: String
    ): Result<VoiceReport> = safeApiCall {
        val dto: VoiceReportResponseDto = client.post("appointments/$appointmentId/voice-reports") {
            headers.remove(HttpHeaders.ContentType)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("audio", audioBytes, Headers.build {
                            append(HttpHeaders.ContentType, "audio/aac")
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                )
            )
        }.body()
        dto.toDomain()
    }

    override suspend fun getReports(appointmentId: String): Result<List<VoiceReport>> = safeApiCall {
        val dtos: List<VoiceReportResponseDto> = client.get("appointments/$appointmentId/voice-reports").body()
        dtos.map { it.toDomain() }
    }

    override suspend fun getReportStatus(appointmentId: String, reportId: String): Result<VoiceReport> = safeApiCall {
        val dto: VoiceReportResponseDto = client.get("appointments/$appointmentId/voice-reports/$reportId").body()
        dto.toDomain()
    }

    override suspend fun deleteReport(appointmentId: String, reportId: String): Result<Unit> = safeApiCall {
        client.delete("appointments/$appointmentId/voice-reports/$reportId")
        Unit
    }

    override fun pollReportStatus(appointmentId: String, reportId: String): Flow<Result<VoiceReport>> = flow {
        while (true) {
            val result = getReportStatus(appointmentId, reportId)
            emit(result)

            val report = result.getOrNull()
            if (report == null || report.status.isTerminal()) {
                break
            }
            delay(5000)
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
