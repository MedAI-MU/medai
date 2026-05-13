package org.example.project.domain.repository.medical_record

import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.medical_record.AnalysisDetailEntity
import org.example.project.domain.model.medical_record.AnalysisEntity
import org.example.project.domain.model.medical_record.MedicalHistoryEntity
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.medical_record.VaccinationEntity


interface MedicalRecordRepository {
    // Patient Profile
    suspend fun getPatientProfile(patientId: String): Result<Patient>
    suspend fun updatePatientMetrics(patientId: String, weight: Double, height: Double): Result<Patient>

    // Lists (All fetching requires patientId context)
    suspend fun getAllergies(patientId: String): Result<List<AllergyEntity>>
    suspend fun getAnalyses(patientId: String): Result<List<AnalysisEntity>>
    suspend fun getVaccinations(patientId: String): Result<List<VaccinationEntity>>
    suspend fun getMedicalHistory(patientId: String): Result<List<MedicalHistoryEntity>>

    // Details
    suspend fun getAnalysisDetails(analysisId: String): Result<List<AnalysisDetailEntity>>
}
