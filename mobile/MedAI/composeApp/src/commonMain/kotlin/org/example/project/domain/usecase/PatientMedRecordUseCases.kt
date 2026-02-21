package org.example.project.domain.usecase

import org.example.project.domain.model.*
import org.example.project.domain.repository.PatientRepository

// --- Allergy UseCases ---
class AddAllergyUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, allergy: AllergyParams) =
        repository.addAllergy(patientId, allergy)
}

class UpdateAllergyUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, allergyId: String, allergy: AllergyParams) =
        repository.updateAllergy(patientId, allergyId, allergy)
}

class DeleteAllergyUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, allergyId: String) =
        repository.deleteAllergy(patientId, allergyId)
}

// --- Chronic Disease UseCases ---
class AddChronicDiseaseUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, disease: ChronicDiseaseParams) =
        repository.addChronicDisease(patientId, disease)
}

class UpdateChronicDiseaseUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, diseaseId: String, disease: ChronicDiseaseParams) =
        repository.updateChronicDisease(patientId, diseaseId, disease)
}

class DeleteChronicDiseaseUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, diseaseId: String) =
        repository.deleteChronicDisease(patientId, diseaseId)
}

// --- Family History UseCases ---
class AddFamilyHistoryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, history: FamilyHistoryParams) =
        repository.addFamilyHistory(patientId, history)
}

class UpdateFamilyHistoryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, historyId: String, history: FamilyHistoryParams) =
        repository.updateFamilyHistory(patientId, historyId, history)
}

class DeleteFamilyHistoryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, historyId: String) =
        repository.deleteFamilyHistory(patientId, historyId)
}

// --- Surgery UseCases ---
class AddSurgeryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, surgery: SurgeryParams) =
        repository.addSurgery(patientId, surgery)
}

class UpdateSurgeryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, surgeryId: String, surgery: SurgeryParams) =
        repository.updateSurgery(patientId, surgeryId, surgery)
}

class DeleteSurgeryUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, surgeryId: String) =
        repository.deleteSurgery(patientId, surgeryId)
}

// --- Emergency Contact UseCases ---
class AddEmergencyContactUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, contact: EmergencyContactParams) =
        repository.addEmergencyContact(patientId, contact)
}

class UpdateEmergencyContactUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, contactId: String, contact: EmergencyContactParams) =
        repository.updateEmergencyContact(patientId, contactId, contact)
}

class DeleteEmergencyContactUseCase(private val repository: PatientRepository) {
    suspend operator fun invoke(patientId: String, contactId: String) =
        repository.deleteEmergencyContact(patientId, contactId)
}
