package org.example.project.domain.repository.diagnosis

import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.diagnosis.CreateDiagnosisParams
import org.example.project.domain.model.diagnosis.UpdateDiagnosisParams

interface DiagnosisRepository {
    suspend fun getDiagnosesForPatient(patientId: String): Result<List<Diagnosis>>
    suspend fun createDiagnosis(patientId: String, params: CreateDiagnosisParams): Result<Diagnosis>
    suspend fun updateDiagnosis(patientId: String, diagnosisId: String, params: UpdateDiagnosisParams): Result<Diagnosis>
    suspend fun deleteDiagnosis(patientId: String, diagnosisId: String): Result<Unit>
}
