package org.example.project.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.remote.dto.diagnosis.DiagnosisResponseDto
import org.example.project.data.remote.dto.diagnosis.CreateDiagnosisRequestDto
import org.example.project.data.remote.dto.diagnosis.UpdateDiagnosisRequestDto

interface DiagnosisApiService {
    suspend fun getDiagnosesForPatient(patientId: String): List<DiagnosisResponseDto>
    suspend fun createDiagnosis(patientId: String, request: CreateDiagnosisRequestDto): DiagnosisResponseDto
    suspend fun updateDiagnosis(patientId: String, diagnosisId: String, request: UpdateDiagnosisRequestDto): DiagnosisResponseDto
    suspend fun deleteDiagnosis(patientId: String, diagnosisId: String)
}

class KtorDiagnosisApiService(
    private val client: HttpClient
) : DiagnosisApiService {

    override suspend fun getDiagnosesForPatient(patientId: String): List<DiagnosisResponseDto> {
        return client.get("diagnosis/$patientId").body()
    }

    override suspend fun createDiagnosis(patientId: String, request: CreateDiagnosisRequestDto): DiagnosisResponseDto {
        return client.post("diagnosis/$patientId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun updateDiagnosis(patientId: String, diagnosisId: String, request: UpdateDiagnosisRequestDto): DiagnosisResponseDto {
        return client.patch("diagnosis/$patientId/$diagnosisId") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun deleteDiagnosis(patientId: String, diagnosisId: String) {
        client.delete("diagnosis/$patientId/$diagnosisId")
    }
}
