package org.example.project.data.repository.mock

import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDate
import org.example.project.domain.model.patient.AllergyEntity
import org.example.project.domain.model.medical_record.AnalysisDetailEntity
import org.example.project.domain.model.medical_record.AnalysisEntity
import org.example.project.domain.model.medical_record.AnalysisStatus
import org.example.project.domain.model.patient.BloodType
import org.example.project.domain.model.patient.Gender
import org.example.project.domain.model.patient.MaritalStatus
import org.example.project.domain.model.medical_record.MedicalHistoryEntity
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.medical_record.VaccinationEntity
import org.example.project.domain.model.medical_record.VaccinationStatus
import org.example.project.domain.repository.medical_record.MedicalRecordRepository

class MockMedicalRecordRepository : MedicalRecordRepository {

    // --- Mock Data Store (using domain entities directly, no DTOs) ---

    private var mockPatient = Patient(
        id = "p1",
        fullName = "John Doe",
        gender = Gender.Male,
        age = 35,
        birthDate = "1990-05-15",
        weight = 70.5,
        height = 17.9,
        bloodType = BloodType.B_NEG,
        maritalStatus = MaritalStatus.Married,
    )

    private val mockAllergies = listOf(
        AllergyEntity("1", "Insulin Allergy", "Skin rash, Itching", "N/A"),
        AllergyEntity("2", "Codeine Allergy", "Dizziness, Nausea", "N/A"),
        AllergyEntity("3", "Pollen Allergy", "Sneezing, Runny nose", "N/A"),
        AllergyEntity("4", "Latex Allergy", "Skin irritation", "N/A")
    )

    private val mockAnalyses = listOf(
        AnalysisEntity("an1", "Blood Test Analysis", LocalDate.parse("2023-10-01"), AnalysisStatus.Completed),
        AnalysisEntity("an2", "Urine Analysis", LocalDate.parse("2023-09-15"), AnalysisStatus.Completed),
        AnalysisEntity("an3", "Lipid Profile", LocalDate.parse("2023-08-20"), AnalysisStatus.Completed),
        AnalysisEntity("an4", "Thyroid Function", LocalDate.parse("2023-07-10"), AnalysisStatus.Pending)
    )

    private val mockVaccinations = listOf(
        VaccinationEntity("v1", "Covid Vaccine", LocalDate.parse("2021-05-15"), null, VaccinationStatus.Done),
        VaccinationEntity("v2", "Flu Shot", LocalDate.parse("2023-11-01"), LocalDate.parse("2024-11-01"), VaccinationStatus.Done),
        VaccinationEntity("v3", "Hepatitis B", LocalDate.parse("2020-01-10"), null, VaccinationStatus.Done)
    )

    private val mockHistory = listOf(
        MedicalHistoryEntity(
            "mh1",
            "Diabetes Type 2",
            "In Control",
            "Diet control and regular exercise.",
            "Dr. Smith"
        ),
        MedicalHistoryEntity("mh2", "Hypertension", "Stable", "Daily medication (Lisinopril 10mg).", "Dr. Jones")
    )

    // --- Implementation ---

    override suspend fun getPatientProfile(patientId: String): Result<Patient> {
        delay(1500) // Simulate network delay
        return Result.success(mockPatient)
    }

    override suspend fun updatePatientMetrics(
        patientId: String,
        weight: Double,
        height: Double
    ): Result<Patient> {
        delay(100)
        // Update local mock store
        mockPatient = mockPatient.copy(weight = weight, height = height)
        return Result.success(mockPatient)
    }

    override suspend fun getAllergies(patientId: String): Result<List<AllergyEntity>> {
        delay(150)
        return Result.success(mockAllergies)
    }

    override suspend fun getAnalyses(patientId: String): Result<List<AnalysisEntity>> {
        delay(150)
        return Result.success(mockAnalyses)
    }

    override suspend fun getVaccinations(patientId: String): Result<List<VaccinationEntity>> {
        delay(150)
        return Result.success(mockVaccinations)
    }

    override suspend fun getMedicalHistory(patientId: String): Result<List<MedicalHistoryEntity>> {
        delay(150)
        return Result.success(mockHistory)
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
