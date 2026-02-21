package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.example.project.data.remote.dto.PatientDto
import org.example.project.data.remote.mapper.*
import org.example.project.domain.model.*
import org.example.project.domain.repository.PatientRepository

class NetworkPatientRepository(
    private val client: HttpClient
) : PatientRepository {

    override suspend fun getPatients(): Result<List<Patient>> = safeApiCallResult {
        val response: List<PatientDto> = client.get("patients").body()
        response.map { it.toEntity() }
    }

    override suspend fun getPatientById(id: String): Result<Patient> = safeApiCallResult {
        val dto: PatientDto = client.get("patients/$id").body()
        dto.toEntity()
    }

    override suspend fun createPatient(patient: CreatePatientParams): Result<Boolean> = safeApiCall {
        client.post("patients") {
            contentType(ContentType.Application.Json)
            setBody(patient.toDto())
        }
    }

    override suspend fun updatePatient(id: String, patient: UpdatePatientParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$id") {
            contentType(ContentType.Application.Json)
            setBody(patient.toDto())
        }
    }

    // Allergies
    override suspend fun addAllergy(patientId: String, allergy: AllergyParams): Result<Boolean> = safeApiCall {
        client.post("patients/$patientId/allergies") {
            contentType(ContentType.Application.Json)
            setBody(allergy.toDto())
        }
    }

    override suspend fun updateAllergy(patientId: String, allergyId: String, allergy: AllergyParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$patientId/allergies/$allergyId") {
            contentType(ContentType.Application.Json)
            setBody(allergy.toUpdateDto())
        }
    }

    override suspend fun deleteAllergy(patientId: String, allergyId: String): Result<Boolean> = safeApiCall {
        client.delete("patients/$patientId/allergies/$allergyId")
    }

    // Chronic Diseases
    override suspend fun addChronicDisease(patientId: String, disease: ChronicDiseaseParams): Result<Boolean> = safeApiCall {
        client.post("patients/$patientId/chronic-diseases") {
            contentType(ContentType.Application.Json)
            setBody(disease.toDto())
        }
    }

    override suspend fun updateChronicDisease(patientId: String, diseaseId: String, disease: ChronicDiseaseParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$patientId/chronic-diseases/$diseaseId") {
            contentType(ContentType.Application.Json)
            setBody(disease.toUpdateDto())
        }
    }

    override suspend fun deleteChronicDisease(patientId: String, diseaseId: String): Result<Boolean> = safeApiCall {
        client.delete("patients/$patientId/chronic-diseases/$diseaseId")
    }

    // Family History
    override suspend fun addFamilyHistory(patientId: String, history: FamilyHistoryParams): Result<Boolean> = safeApiCall {
        client.post("patients/$patientId/family-histories") {
            contentType(ContentType.Application.Json)
            setBody(history.toDto())
        }
    }

    override suspend fun updateFamilyHistory(patientId: String, historyId: String, history: FamilyHistoryParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$patientId/family-histories/$historyId") {
            contentType(ContentType.Application.Json)
            setBody(history.toUpdateDto())
        }
    }

    override suspend fun deleteFamilyHistory(patientId: String, historyId: String): Result<Boolean> = safeApiCall {
        client.delete("patients/$patientId/family-histories/$historyId")
    }

    // Surgeries
    override suspend fun addSurgery(patientId: String, surgery: SurgeryParams): Result<Boolean> = safeApiCall {
        client.post("patients/$patientId/surgeries") {
            contentType(ContentType.Application.Json)
            setBody(surgery.toDto())
        }
    }

    override suspend fun updateSurgery(patientId: String, surgeryId: String, surgery: SurgeryParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$patientId/surgeries/$surgeryId") {
            contentType(ContentType.Application.Json)
            setBody(surgery.toUpdateDto())
        }
    }

    override suspend fun deleteSurgery(patientId: String, surgeryId: String): Result<Boolean> = safeApiCall {
        client.delete("patients/$patientId/surgeries/$surgeryId")
    }

    // Emergency Contacts
    override suspend fun addEmergencyContact(patientId: String, contact: EmergencyContactParams): Result<Boolean> = safeApiCall {
        client.post("patients/$patientId/emergency-contacts") {
            contentType(ContentType.Application.Json)
            setBody(contact.toDto())
        }
    }

    override suspend fun updateEmergencyContact(patientId: String, contactId: String, contact: EmergencyContactParams): Result<Boolean> = safeApiCall {
        client.patch("patients/$patientId/emergency-contacts/$contactId") {
            contentType(ContentType.Application.Json)
            setBody(contact.toUpdateDto())
        }
    }

    override suspend fun deleteEmergencyContact(patientId: String, contactId: String): Result<Boolean> = safeApiCall {
        client.delete("patients/$patientId/emergency-contacts/$contactId")
    }

    private suspend fun safeApiCall(call: suspend () -> Unit): Result<Boolean> {
        return try {
            call()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun <T> safeApiCallResult(call: suspend () -> T): Result<T> {
        return try {
            Result.success(call())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
