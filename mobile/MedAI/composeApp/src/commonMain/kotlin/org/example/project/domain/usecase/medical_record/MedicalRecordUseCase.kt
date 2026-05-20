package org.example.project.domain.usecase.medical_record


import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.medical_record.AnalysisDetailEntity
import org.example.project.domain.model.medical_record.AnalysisEntity
import org.example.project.domain.model.medical_record.MedicalHistoryEntity
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.medical_record.VaccinationEntity
import org.example.project.domain.repository.medical_record.MedicalRecordRepository

class GetPatientProfileUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Result<Patient> {
        return repository.getPatientProfile(patientId)
    }
}

class UpdatePatientMetricsUseCase(private val repository: MedicalRecordRepository) {
    data class Params(val patientId: String, val weight: Double, val height: Double)

    suspend operator fun invoke(params: Params): Result<Patient> {
        return repository.updatePatientMetrics(params.patientId, params.weight, params.height)
    }
}

// --- Lists ---
class GetAllergiesUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Result<List<AllergyEntity>> = repository.getAllergies(patientId)
}

class GetAnalysesUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Result<List<AnalysisEntity>> = repository.getAnalyses(patientId)
}

class GetVaccinationsUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Result<List<VaccinationEntity>> = repository.getVaccinations(patientId)
}

class GetMedicalHistoryUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(patientId: String): Result<List<MedicalHistoryEntity>> = repository.getMedicalHistory(patientId)
}

// --- Details ---
class GetAnalysisDetailsUseCase(private val repository: MedicalRecordRepository) {
    suspend operator fun invoke(analysisId: String): Result<List<AnalysisDetailEntity>> = repository.getAnalysisDetails(analysisId)
}
