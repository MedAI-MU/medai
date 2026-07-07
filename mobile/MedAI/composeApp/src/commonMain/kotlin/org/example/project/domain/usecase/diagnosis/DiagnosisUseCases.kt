package org.example.project.domain.usecase.diagnosis

import org.example.project.domain.model.diagnosis.Diagnosis
import org.example.project.domain.model.diagnosis.CreateDiagnosisParams
import org.example.project.domain.model.diagnosis.UpdateDiagnosisParams
import org.example.project.domain.repository.diagnosis.DiagnosisRepository

class GetPatientDiagnosesUseCase(
    private val repository: DiagnosisRepository
) {
    suspend operator fun invoke(patientId: String): Result<List<Diagnosis>> =
        repository.getDiagnosesForPatient(patientId)
}

class CreateDiagnosisUseCase(
    private val repository: DiagnosisRepository
) {
    suspend operator fun invoke(patientId: String, params: CreateDiagnosisParams): Result<Diagnosis> =
        repository.createDiagnosis(patientId, params)
}

class UpdateDiagnosisUseCase(
    private val repository: DiagnosisRepository
) {
    suspend operator fun invoke(patientId: String, diagnosisId: String, params: UpdateDiagnosisParams): Result<Diagnosis> =
        repository.updateDiagnosis(patientId, diagnosisId, params)
}

class DeleteDiagnosisUseCase(
    private val repository: DiagnosisRepository
) {
    suspend operator fun invoke(patientId: String, diagnosisId: String): Result<Unit> =
        repository.deleteDiagnosis(patientId, diagnosisId)
}
