package org.example.project.data.repository.scans

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
import org.example.project.data.remote.dto.scans.ScanResponseDto
import org.example.project.data.remote.dto.scans.ReportResponseDto
import org.example.project.data.remote.mapper.toDomain
import org.example.project.domain.model.scans.Scan
import org.example.project.domain.model.scans.Report
import org.example.project.domain.repository.scans.ScansRepository

class NetworkScansRepository(
    private val client: HttpClient
) : ScansRepository {

    override suspend fun getScans(): Result<List<Scan>> = safeApiCall {
        val dtos: List<ScanResponseDto> = client.get("scans").body()
        dtos.map { it.toDomain() }
    }

    override suspend fun getScanDetails(patientUserId: String, scanId: String): Result<Scan> = safeApiCall {
        val dto: ScanResponseDto = client.get("scans/$patientUserId/$scanId").body()
        dto.toDomain()
    }

    override suspend fun uploadScan(
        patientUserId: String,
        appointmentId: String?,
        images: List<ByteArray>
    ): Result<Scan> = safeApiCall {
        val dto: ScanResponseDto = client.post("scans/$patientUserId") {
            headers.remove(HttpHeaders.ContentType)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        images.forEachIndexed { index, bytes ->
                            append("images", bytes, Headers.build {
                                append(HttpHeaders.ContentType, "image/png")
                                append(HttpHeaders.ContentDisposition, "filename=\"scan_$index.png\"")
                            })
                        }
                        appointmentId?.let {
                            append("appointmentId", it)
                        }
                    }
                )
            )
        }.body()
        dto.toDomain()
    }

    override suspend fun deleteScan(patientUserId: String, scanId: String): Result<Unit> = safeApiCall {
        client.delete("scans/$patientUserId/$scanId")
    }

    override suspend fun getScanReports(patientUserId: String, scanId: String): Result<List<Report>> = safeApiCall {
        val dtos: List<ReportResponseDto> = client.get("scans/$patientUserId/$scanId/reports").body()
        dtos.map { it.toDomain() }
    }

    override suspend fun uploadReport(
        patientUserId: String,
        scanId: String,
        fileBytes: ByteArray,
        fileName: String
    ): Result<Report> = safeApiCall {
        val contentType = if (fileName.endsWith(".pdf", ignoreCase = true)) "application/pdf" else "image/png"
        val dto: ReportResponseDto = client.post("scans/$patientUserId/$scanId/reports") {
            headers.remove(HttpHeaders.ContentType)
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("file", fileBytes, Headers.build {
                            append(HttpHeaders.ContentType, contentType)
                            append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                        })
                    }
                )
            )
        }.body()
        dto.toDomain()
    }

    override suspend fun deleteReport(patientUserId: String, reportId: String): Result<Unit> = safeApiCall {
        client.delete("reports/$patientUserId/$reportId")
    }

    override suspend fun getScanImageFile(patientUserId: String, imageId: String): Result<ByteArray> = safeApiCall {
        client.get("scans/images/$patientUserId/$imageId/file").body()
    }

    override suspend fun getReportFile(patientUserId: String, reportId: String): Result<ByteArray> = safeApiCall {
        client.get("reports/$patientUserId/$reportId/file").body()
    }

    private suspend fun <T> safeApiCall(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
