package org.example.project.data.repository

import org.example.project.data.remote.api.DiagnosisApiService
import org.example.project.data.remote.mapper.toDomain
import org.example.project.data.remote.mapper.toDto
import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.diagnosis.CreateDiagnosisParams
import org.example.project.domain.model.diagnosis.UpdateDiagnosisParams
import org.example.project.domain.repository.diagnosis.DiagnosisRepository

class NetworkDiagnosisRepository(
    private val apiService: DiagnosisApiService
) : DiagnosisRepository {

    override suspend fun getDiagnosesForPatient(patientId: String): Result<List<Diagnosis>> = safeCall {
        apiService.getDiagnosesForPatient(patientId).map { it.toDomain() }
    }

    override suspend fun createDiagnosis(patientId: String, params: CreateDiagnosisParams): Result<Diagnosis> = safeCall {
        apiService.createDiagnosis(patientId, params.toDto()).toDomain()
    }

    override suspend fun updateDiagnosis(
        patientId: String,
        diagnosisId: String,
        params: UpdateDiagnosisParams
    ): Result<Diagnosis> = safeCall {
        apiService.updateDiagnosis(patientId, diagnosisId, params.toDto()).toDomain()
    }

    override suspend fun deleteDiagnosis(patientId: String, diagnosisId: String): Result<Unit> = safeCall {
        apiService.deleteDiagnosis(patientId, diagnosisId)
    }

    private inline fun <T> safeCall(action: () -> T): Result<T> {
        return try {
            Result.success(action())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
