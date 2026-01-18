package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.example.project.data.remote.dto.AllergyDto
import org.example.project.data.remote.dto.AnalysisDto
import org.example.project.data.remote.dto.MedicalHistoryDto
import org.example.project.data.remote.dto.PatientDto
import org.example.project.data.remote.dto.VaccinationDto
import org.example.project.data.remote.mapper.toEntity
import org.example.project.domain.model.AllergyEntity
import org.example.project.domain.model.AnalysisDetailEntity
import org.example.project.domain.model.AnalysisEntity
import org.example.project.domain.model.MedicalHistoryEntity
import org.example.project.domain.model.PatientEntity
import org.example.project.domain.model.VaccinationEntity
import org.example.project.domain.repository.MedicalRecordRepository
import kotlin.collections.map

class MockMedicalRecordRepository : MedicalRecordRepository {

    // --- Mock Data Store ---

    private var patientDto = PatientDto(
        id = "p1",
        full_name = "Jane Doe",
        gender_code = "F",
        age = 24,
        weight_kg = 55.0,
        height_cm = 165.0,
        blood_group = "AB+"
    )

    private val allergyDtos = listOf(
        AllergyDto("a1", "Insulin Allergy", "Skin rash, Itching", "2022-05-10"),
        AllergyDto("a2", "Codeine Allergy", "Dizziness, Nausea", "2021-03-15"),
        AllergyDto("a3", "Pollen Allergy", "Sneezing, Runny nose", "2020-04-01"),
        AllergyDto("a4", "Latex Allergy", "Skin irritation", "2019-08-20")
    )

    private val analysisDtos = listOf(
        AnalysisDto("an1", "Blood Test Analysis", "2023-10-01", 1),
        AnalysisDto("an2", "Urine Analysis", "2023-09-15", 1),
        AnalysisDto("an3", "Lipid Profile", "2023-08-20", 1),
        AnalysisDto("an4", "Thyroid Function", "2023-07-10", 0)
    )

    private val vaccinationDtos = listOf(
        VaccinationDto("v1", "Covid Vaccine", "2021-05-15", null, true),
        VaccinationDto("v2", "Flu Shot", "2023-11-01", "2024-11-01", true),
        VaccinationDto("v3", "Hepatitis B", "2020-01-10", null, true)
    )

    private val historyDtos = listOf(
        MedicalHistoryDto(
            "mh1",
            "Diabetes Type 2",
            "In Control",
            "Diet control and regular exercise.",
            "Dr. Smith"
        ),
        MedicalHistoryDto("mh2", "Hypertension", "Stable", "Daily medication (Lisinopril 10mg).", "Dr. Jones")
    )

    // --- Implementation ---

    override suspend fun getPatientProfile(patientId: String): Result<PatientEntity> {
        delay(1500) // Simulate network delay
        return Result.success(patientDto.toEntity())
    }

    override suspend fun updatePatientMetrics(
        patientId: String,
        weight: Double,
        height: Double
    ): Result<PatientEntity> {
        delay(100)
        // Update local mock store
        patientDto = patientDto.copy(weight_kg = weight, height_cm = height)
        return Result.success(patientDto.toEntity())
    }

    override suspend fun getAllergies(patientId: String): Result<List<AllergyEntity>> {
        delay(150)
        return Result.success(allergyDtos.map { it.toEntity() })
    }

    override suspend fun getAnalyses(patientId: String): Result<List<AnalysisEntity>> {
        delay(150)
        return Result.success(analysisDtos.map { it.toEntity() })
    }

    override suspend fun getVaccinations(patientId: String): Result<List<VaccinationEntity>> {
        delay(150)
        return Result.success(vaccinationDtos.map { it.toEntity() })
    }

    override suspend fun getMedicalHistory(patientId: String): Result<List<MedicalHistoryEntity>> {
        delay(150)
        return Result.success(historyDtos.map { it.toEntity() })
    }

    override suspend fun getAnalysisDetails(analysisId: String): Result<List<AnalysisDetailEntity>> {
        delay(100)
        // Hardcoded details for demo
        val details = listOf(
            AnalysisDetailEntity("d1", analysisId, "Glucose", "12", "mg/dL", "70-100"),
            AnalysisDetailEntity("d2", analysisId, "Electrolytes", "Normal", null, null),
            AnalysisDetailEntity("d3", analysisId, "Liver Enzymes", "Normal", null, null)
        )
        return Result.success(details)
    }
}
