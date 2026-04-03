package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.domain.model.CreateMedicalReportRequest
import org.example.project.domain.model.MedicalReport
import org.example.project.domain.repository.MedicalReportsRepository

class NetworkMedicalReportsRepository(
    private val client: HttpClient
) : MedicalReportsRepository {

    override suspend fun getReports(patientId: Int, token: String): Result<List<MedicalReport>> {
        return try {
            val response = client.get("http://10.0.2.2:3000/patients/$patientId/reports") {
                header("Authorization", "Bearer $token")
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createReport(
        patientId: Int,
        token: String,
        request: CreateMedicalReportRequest
    ): Result<MedicalReport> {
        return try {
            val response = client.post("http://10.0.2.2:3000/patients/$patientId/reports") {
                header("Authorization", "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(request)
            }
            Result.success(response.body())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
