package org.example.project.presentation.shared.records

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.domain.model.patient.*
import org.example.project.domain.repository.patient.PatientRepository
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.usecase.medical_record.*
import org.example.project.core.presentation.mvi.MviScreenModel

class SharedMedicalRecordViewModel(
    private val patientIdArg: String?,
    private val patientRepository: PatientRepository,
    private val sessionManager: UserSessionManager,
    private val addAllergyUseCase: AddAllergyUseCase,
    private val updateAllergyUseCase: UpdateAllergyUseCase,
    private val deleteAllergyUseCase: DeleteAllergyUseCase,
    private val addChronicDiseaseUseCase: AddChronicDiseaseUseCase,
    private val updateChronicDiseaseUseCase: UpdateChronicDiseaseUseCase,
    private val deleteChronicDiseaseUseCase: DeleteChronicDiseaseUseCase,
    private val addSurgeryUseCase: AddSurgeryUseCase,
    private val updateSurgeryUseCase: UpdateSurgeryUseCase,
    private val deleteSurgeryUseCase: DeleteSurgeryUseCase,
    private val addFamilyHistoryUseCase: AddFamilyHistoryUseCase,
    private val updateFamilyHistoryUseCase: UpdateFamilyHistoryUseCase,
    private val deleteFamilyHistoryUseCase: DeleteFamilyHistoryUseCase,
    private val addEmergencyContactUseCase: AddEmergencyContactUseCase,
    private val updateEmergencyContactUseCase: UpdateEmergencyContactUseCase,
    private val deleteEmergencyContactUseCase: DeleteEmergencyContactUseCase
) : MviScreenModel<SharedMedicalRecordState, SharedMedicalRecordEvent, SharedMedicalRecordEffect>(
    initialState = SharedMedicalRecordState()
) {
    private var currentPatientId = ""

    init {
        screenModelScope.launch {
            val id = patientIdArg ?: sessionManager.getUserId()
            if (id != null) {
                currentPatientId = id
                onEvent(SharedMedicalRecordEvent.LoadData)
            } else {
                setState { copy(error = "User session expired or patient ID missing") }
            }
        }
    }

    override fun onEvent(event: SharedMedicalRecordEvent) {
        when (event) {
            SharedMedicalRecordEvent.LoadData -> loadAllData()
            is SharedMedicalRecordEvent.UpdatePatientInfo -> updatePatientInfo(event.params)
            is SharedMedicalRecordEvent.UpdateBasicInfo -> updatePatientInfo(
                UpdatePatientParams(weight = event.weight, height = event.height)
            )

            // Allergies
            is SharedMedicalRecordEvent.AddAllergy -> addAllergy(event.params)
            is SharedMedicalRecordEvent.EditAllergy -> editAllergy(event.id, event.params)
            is SharedMedicalRecordEvent.DeleteAllergy -> deleteAllergy(event.id)

            // Chronic Diseases
            is SharedMedicalRecordEvent.AddChronicDisease -> addChronicDisease(event.params)
            is SharedMedicalRecordEvent.EditChronicDisease -> editChronicDisease(event.id, event.params)
            is SharedMedicalRecordEvent.DeleteChronicDisease -> deleteChronicDisease(event.id)

            // Surgeries
            is SharedMedicalRecordEvent.AddSurgery -> addSurgery(event.params)
            is SharedMedicalRecordEvent.EditSurgery -> editSurgery(event.id, event.params)
            is SharedMedicalRecordEvent.DeleteSurgery -> deleteSurgery(event.id)

            // Family History
            is SharedMedicalRecordEvent.AddFamilyHistory -> addFamilyHistory(event.params)
            is SharedMedicalRecordEvent.EditFamilyHistory -> editFamilyHistory(event.id, event.params)
            is SharedMedicalRecordEvent.DeleteFamilyHistory -> deleteFamilyHistory(event.id)

            // Emergency Contacts
            is SharedMedicalRecordEvent.AddEmergencyContact -> addEmergencyContact(event.params)
            is SharedMedicalRecordEvent.EditEmergencyContact -> editEmergencyContact(event.id, event.params)
            is SharedMedicalRecordEvent.DeleteEmergencyContact -> deleteEmergencyContact(event.id)

            is SharedMedicalRecordEvent.AnalysisClicked -> sendEffect(SharedMedicalRecordEffect.NavigateToAnalysisDetails(event.analysisId))
            SharedMedicalRecordEvent.AddRecordClicked -> sendEffect(SharedMedicalRecordEffect.NavigateToAddRecord)
        }
    }

    private fun loadAllData(silent: Boolean = false) {
        screenModelScope.launch {
            if (!silent) setState { copy(isLoading = true, error = null) }

            val profileResult = patientRepository.getPatientById(currentPatientId)

            if (profileResult.isSuccess) {
                val patient = profileResult.getOrNull()
                setState {
                    copy(
                        isLoading = false,
                        patientProfile = patient,
                        allergies = patient?.allergies ?: emptyList(),
                        analyses = emptyList(), // Populate these from repositories when available
                        vaccinations = emptyList(),
                        medicalHistory = emptyList()
                    )
                }
            } else {
                val errorMsg = profileResult.exceptionOrNull()?.message ?: "Failed to load profile"
                setState {
                    copy(isLoading = false, error = errorMsg)
                }
                sendEffect(SharedMedicalRecordEffect.ShowError(errorMsg))
            }
        }
    }

    private fun updatePatientInfo(params: UpdatePatientParams) {
        screenModelScope.launch {
            setState { copy(isUpdatingBasicInfo = true) }
            patientRepository.updatePatient(currentPatientId, params).fold(
                onSuccess = {
                    loadAllData(silent = true)
                    setState { copy(isUpdatingBasicInfo = false) }
                    sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Profile updated"))
                },
                onFailure = { error ->
                    setState { copy(isUpdatingBasicInfo = false) }
                    sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update: ${error.message}"))
                }
            )
        }
    }

    // --- Mutation Handlers ---

    private fun addAllergy(params: AllergyParams) {
        screenModelScope.launch {
            setState { copy(isProcessingAllergy = true) }
            addAllergyUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Allergy added"))
                setState { copy(isProcessingAllergy = false) }
            }.onFailure {
                setState { copy(isProcessingAllergy = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to add allergy"))
            }
        }
    }

    private fun editAllergy(id: String, params: AllergyParams) {
        screenModelScope.launch {
            setState { copy(isProcessingAllergy = true) }
            updateAllergyUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Allergy updated"))
                setState { copy(isProcessingAllergy = false) }
            }.onFailure {
                setState { copy(isProcessingAllergy = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update allergy"))
            }
        }
    }

    private fun deleteAllergy(id: String) {
        screenModelScope.launch {
            setState { copy(isProcessingAllergy = true) }
            deleteAllergyUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Allergy deleted"))
                setState { copy(isProcessingAllergy = false) }
            }.onFailure {
                setState { copy(isProcessingAllergy = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to delete allergy"))
            }
        }
    }

    private fun addChronicDisease(params: ChronicDiseaseParams) {
        screenModelScope.launch {
            setState { copy(isProcessingDisease = true) }
            addChronicDiseaseUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Chronic disease added"))
                setState { copy(isProcessingDisease = false) }
            }.onFailure {
                setState { copy(isProcessingDisease = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to add chronic disease"))
            }
        }
    }

    private fun editChronicDisease(id: String, params: ChronicDiseaseParams) {
        screenModelScope.launch {
            setState { copy(isProcessingDisease = true) }
            updateChronicDiseaseUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Chronic disease updated"))
                setState { copy(isProcessingDisease = false) }
            }.onFailure {
                setState { copy(isProcessingDisease = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update chronic disease"))
            }
        }
    }

    private fun deleteChronicDisease(id: String) {
        screenModelScope.launch {
            setState { copy(isProcessingDisease = true) }
            deleteChronicDiseaseUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Chronic disease deleted"))
                setState { copy(isProcessingDisease = false) }
            }.onFailure {
                setState { copy(isProcessingDisease = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to delete chronic disease"))
            }
        }
    }

    private fun addSurgery(params: SurgeryParams) {
        screenModelScope.launch {
            setState { copy(isProcessingSurgery = true) }
            addSurgeryUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Surgery added"))
                setState { copy(isProcessingSurgery = false) }
            }.onFailure {
                setState { copy(isProcessingSurgery = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to add surgery"))
            }
        }
    }

    private fun editSurgery(id: String, params: SurgeryParams) {
        screenModelScope.launch {
            setState { copy(isProcessingSurgery = true) }
            updateSurgeryUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Surgery updated"))
                setState { copy(isProcessingSurgery = false) }
            }.onFailure {
                setState { copy(isProcessingSurgery = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update surgery"))
            }
        }
    }

    private fun deleteSurgery(id: String) {
        screenModelScope.launch {
            setState { copy(isProcessingSurgery = true) }
            deleteSurgeryUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Surgery deleted"))
                setState { copy(isProcessingSurgery = false) }
            }.onFailure {
                setState { copy(isProcessingSurgery = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to delete surgery"))
            }
        }
    }

    private fun addFamilyHistory(params: FamilyHistoryParams) {
        screenModelScope.launch {
            setState { copy(isProcessingFamilyHistory = true) }
            addFamilyHistoryUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Family history added"))
                setState { copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                setState { copy(isProcessingFamilyHistory = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to add family history"))
            }
        }
    }

    private fun editFamilyHistory(id: String, params: FamilyHistoryParams) {
        screenModelScope.launch {
            setState { copy(isProcessingFamilyHistory = true) }
            updateFamilyHistoryUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Family history updated"))
                setState { copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                setState { copy(isProcessingFamilyHistory = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update family history"))
            }
        }
    }

    private fun deleteFamilyHistory(id: String) {
        screenModelScope.launch {
            setState { copy(isProcessingFamilyHistory = true) }
            deleteFamilyHistoryUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Family history deleted"))
                setState { copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                setState { copy(isProcessingFamilyHistory = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to delete family history"))
            }
        }
    }

    private fun addEmergencyContact(params: EmergencyContactParams) {
        screenModelScope.launch {
            setState { copy(isProcessingEmergencyContact = true) }
            addEmergencyContactUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Emergency contact added"))
                setState { copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                setState { copy(isProcessingEmergencyContact = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to add emergency contact"))
            }
        }
    }

    private fun editEmergencyContact(id: String, params: EmergencyContactParams) {
        screenModelScope.launch {
            setState { copy(isProcessingEmergencyContact = true) }
            updateEmergencyContactUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Emergency contact updated"))
                setState { copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                setState { copy(isProcessingEmergencyContact = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to update emergency contact"))
            }
        }
    }

    private fun deleteEmergencyContact(id: String) {
        screenModelScope.launch {
            setState { copy(isProcessingEmergencyContact = true) }
            deleteEmergencyContactUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Emergency contact deleted"))
                setState { copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                setState { copy(isProcessingEmergencyContact = false) }
                sendEffect(SharedMedicalRecordEffect.ShowSnackbar("Failed to delete emergency contact"))
            }
        }
    }
}
