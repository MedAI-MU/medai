package org.example.project.domain.repository.patient

import org.example.project.domain.model.auth.*
import org.example.project.domain.model.appointment.*
import org.example.project.domain.model.chat.*
import org.example.project.domain.model.doctor.*
import org.example.project.domain.model.home.*
import org.example.project.domain.model.medical_record.*
import org.example.project.domain.model.notification.*
import org.example.project.domain.model.patient.*
import org.example.project.domain.model.specialty.*

interface PatientRepository {
    suspend fun getPatients(): Result<List<Patient>>
    suspend fun getPatientById(id: String): Result<Patient>
    suspend fun createPatient(patient: CreatePatientParams): Result<Boolean>
    suspend fun updatePatient(id: String, patient: UpdatePatientParams): Result<Boolean>

    // Allergies
    suspend fun addAllergy(patientId: String, allergy: AllergyParams): Result<Boolean>
    suspend fun updateAllergy(patientId: String, allergyId: String, allergy: AllergyParams): Result<Boolean>
    suspend fun deleteAllergy(patientId: String, allergyId: String): Result<Boolean>

    // Chronic Diseases
    suspend fun addChronicDisease(patientId: String, disease: ChronicDiseaseParams): Result<Boolean>
    suspend fun updateChronicDisease(patientId: String, diseaseId: String, disease: ChronicDiseaseParams): Result<Boolean>
    suspend fun deleteChronicDisease(patientId: String, diseaseId: String): Result<Boolean>

    // Family History
    suspend fun addFamilyHistory(patientId: String, history: FamilyHistoryParams): Result<Boolean>
    suspend fun updateFamilyHistory(patientId: String, historyId: String, history: FamilyHistoryParams): Result<Boolean>
    suspend fun deleteFamilyHistory(patientId: String, historyId: String): Result<Boolean>

    // Surgeries
    suspend fun addSurgery(patientId: String, surgery: SurgeryParams): Result<Boolean>
    suspend fun updateSurgery(patientId: String, surgeryId: String, surgery: SurgeryParams): Result<Boolean>
    suspend fun deleteSurgery(patientId: String, surgeryId: String): Result<Boolean>

    // Emergency Contacts
    suspend fun addEmergencyContact(patientId: String, contact: EmergencyContactParams): Result<Boolean>
    suspend fun updateEmergencyContact(patientId: String, contactId: String, contact: EmergencyContactParams): Result<Boolean>
    suspend fun deleteEmergencyContact(patientId: String, contactId: String): Result<Boolean>
}
