package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.remote.dto.CreatePatientDto
import org.example.project.data.remote.dto.PatientDto
import org.example.project.data.remote.dto.UpdatePatientDto
import org.example.project.data.remote.mapper.toEntity
import org.example.project.domain.model.Patient
import org.example.project.domain.repository.PatientRepository

class NetworkPatientRepository(
    private val client: HttpClient
) : PatientRepository {

    override suspend fun getPatients(): Result<List<Patient>> {
        return try {
            val response: List<PatientDto> = client.get("patients").body()
            Result.success(response.map { it.toEntity() })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPatientById(id: String): Result<Patient> {
        return try {
            val dto: PatientDto = client.get("patients/$id").body()
            Result.success(dto.toEntity())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createPatient(patient: CreatePatientDto): Result<Boolean> {
        return try {
            client.post("patients") {
                contentType(ContentType.Application.Json)
                setBody(patient)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePatient(id: String, patient: UpdatePatientDto): Result<Boolean> {
        return try {
            client.patch("patients/$id") {
                contentType(ContentType.Application.Json)
                setBody(patient)
            }
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
