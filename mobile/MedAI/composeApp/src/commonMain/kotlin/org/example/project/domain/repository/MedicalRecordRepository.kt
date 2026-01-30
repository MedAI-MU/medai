package org.example.project.domain.repository

import org.example.project.domain.model.AllergyEntity
import org.example.project.domain.model.AnalysisDetailEntity
import org.example.project.domain.model.AnalysisEntity
import org.example.project.domain.model.MedicalHistoryEntity
import org.example.project.domain.model.PatientEntity
import org.example.project.domain.model.VaccinationEntity


interface MedicalRecordRepository {
    // Patient Profile
    suspend fun getPatientProfile(patientId: String): Result<PatientEntity>
    suspend fun updatePatientMetrics(patientId: String, weight: Double, height: Double): Result<PatientEntity>

    // Lists (All fetching requires patientId context)
    suspend fun getAllergies(patientId: String): Result<List<AllergyEntity>>
    suspend fun getAnalyses(patientId: String): Result<List<AnalysisEntity>>
    suspend fun getVaccinations(patientId: String): Result<List<VaccinationEntity>>
    suspend fun getMedicalHistory(patientId: String): Result<List<MedicalHistoryEntity>>

    // Details
    suspend fun getAnalysisDetails(analysisId: String): Result<List<AnalysisDetailEntity>>
}
