package org.example.project.presentation.shared.records

import org.example.project.domain.model.patient.*
import org.example.project.domain.model.medical_record.AnalysisEntity
import org.example.project.domain.model.medical_record.VaccinationEntity
import org.example.project.domain.model.medical_record.MedicalHistoryEntity

data class SharedMedicalRecordState(
    val isLoading: Boolean = false,
    val patientProfile: Patient? = null,
    val allergies: List<AllergyEntity> = emptyList(),
    val analyses: List<AnalysisEntity> = emptyList(),
    val vaccinations: List<VaccinationEntity> = emptyList(),
    val medicalHistory: List<MedicalHistoryEntity> = emptyList(),
    val error: String? = null,

    // Individual loading states
    val isUpdatingBasicInfo: Boolean = false,
    val isProcessingAllergy: Boolean = false,
    val isProcessingDisease: Boolean = false,
    val isProcessingSurgery: Boolean = false,
    val isProcessingFamilyHistory: Boolean = false,
    val isProcessingEmergencyContact: Boolean = false
)

sealed class SharedMedicalRecordEvent {
    object LoadData : SharedMedicalRecordEvent()

    // Profile Updates
    data class UpdatePatientInfo(val params: UpdatePatientParams) : SharedMedicalRecordEvent()
    data class UpdateBasicInfo(val weight: Double, val height: Double) : SharedMedicalRecordEvent() // from Doctor

    // Allergies
    data class AddAllergy(val params: AllergyParams) : SharedMedicalRecordEvent()
    data class EditAllergy(val id: String, val params: AllergyParams) : SharedMedicalRecordEvent()
    data class DeleteAllergy(val id: String) : SharedMedicalRecordEvent()

    // Chronic Diseases
    data class AddChronicDisease(val params: ChronicDiseaseParams) : SharedMedicalRecordEvent()
    data class EditChronicDisease(val id: String, val params: ChronicDiseaseParams) : SharedMedicalRecordEvent()
    data class DeleteChronicDisease(val id: String) : SharedMedicalRecordEvent()

    // Surgeries
    data class AddSurgery(val params: SurgeryParams) : SharedMedicalRecordEvent()
    data class EditSurgery(val id: String, val params: SurgeryParams) : SharedMedicalRecordEvent()
    data class DeleteSurgery(val id: String) : SharedMedicalRecordEvent()

    // Family History
    data class AddFamilyHistory(val params: FamilyHistoryParams) : SharedMedicalRecordEvent()
    data class EditFamilyHistory(val id: String, val params: FamilyHistoryParams) : SharedMedicalRecordEvent()
    data class DeleteFamilyHistory(val id: String) : SharedMedicalRecordEvent()

    // Emergency Contact
    data class AddEmergencyContact(val params: EmergencyContactParams) : SharedMedicalRecordEvent()
    data class EditEmergencyContact(val id: String, val params: EmergencyContactParams) : SharedMedicalRecordEvent()
    data class DeleteEmergencyContact(val id: String) : SharedMedicalRecordEvent()

    // Navigation Intents
    data class AnalysisClicked(val analysisId: String) : SharedMedicalRecordEvent()
    object AddRecordClicked : SharedMedicalRecordEvent()
}

sealed class SharedMedicalRecordEffect {
    data class ShowSnackbar(val message: String) : SharedMedicalRecordEffect()
    data class ShowError(val message: String) : SharedMedicalRecordEffect()
    data class NavigateToAnalysisDetails(val analysisId: String) : SharedMedicalRecordEffect()
    object NavigateToAddRecord : SharedMedicalRecordEffect()
}
