package org.example.project.presentation.recordScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.AllergyEntity
import org.example.project.domain.model.AllergyParams
import org.example.project.domain.model.AnalysisEntity
import org.example.project.domain.model.ChronicDiseaseParams
import org.example.project.domain.model.EmergencyContactParams
import org.example.project.domain.model.FamilyHistoryParams
import org.example.project.domain.model.MedicalHistoryEntity
import org.example.project.domain.model.Patient
import org.example.project.domain.model.SurgeryParams
import org.example.project.domain.model.UpdatePatientParams
import org.example.project.domain.model.VaccinationEntity
import org.example.project.domain.repository.PatientRepository
import org.example.project.domain.repository.UserSessionManager
import org.example.project.domain.usecase.AddAllergyUseCase
import org.example.project.domain.usecase.AddChronicDiseaseUseCase
import org.example.project.domain.usecase.AddEmergencyContactUseCase
import org.example.project.domain.usecase.AddFamilyHistoryUseCase
import org.example.project.domain.usecase.AddSurgeryUseCase
import org.example.project.domain.usecase.DeleteAllergyUseCase
import org.example.project.domain.usecase.DeleteChronicDiseaseUseCase
import org.example.project.domain.usecase.DeleteEmergencyContactUseCase
import org.example.project.domain.usecase.DeleteFamilyHistoryUseCase
import org.example.project.domain.usecase.DeleteSurgeryUseCase
import org.example.project.domain.usecase.UpdateAllergyUseCase
import org.example.project.domain.usecase.UpdateChronicDiseaseUseCase
import org.example.project.domain.usecase.UpdateEmergencyContactUseCase
import org.example.project.domain.usecase.UpdateFamilyHistoryUseCase
import org.example.project.domain.usecase.UpdateSurgeryUseCase

data class MedicalRecordState(
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

// --- EVENTS (User Intents) ---
sealed class MedicalRecordEvent {
    object LoadFullRecords : MedicalRecordEvent()
    object Refresh : MedicalRecordEvent()

    // Profile Updates
    data class UpdatePatientInfo(val params: UpdatePatientParams) : MedicalRecordEvent()

    // Allergies
    data class AddAllergy(val params: AllergyParams) : MedicalRecordEvent()
    data class EditAllergy(val id: String, val params: AllergyParams) : MedicalRecordEvent()
    data class DeleteAllergy(val id: String) : MedicalRecordEvent()

    // Chronic Diseases
    data class AddChronicDisease(val params: ChronicDiseaseParams) : MedicalRecordEvent()
    data class EditChronicDisease(val id: String, val params: ChronicDiseaseParams) : MedicalRecordEvent()
    data class DeleteChronicDisease(val id: String) : MedicalRecordEvent()

    // Surgeries
    data class AddSurgery(val params: SurgeryParams) : MedicalRecordEvent()
    data class EditSurgery(val id: String, val params: SurgeryParams) : MedicalRecordEvent()
    data class DeleteSurgery(val id: String) : MedicalRecordEvent()

    // Family History
    data class AddFamilyHistory(val params: FamilyHistoryParams) : MedicalRecordEvent()
    data class EditFamilyHistory(val id: String, val params: FamilyHistoryParams) : MedicalRecordEvent()
    data class DeleteFamilyHistory(val id: String) : MedicalRecordEvent()

    // Emergency Contact
    data class AddEmergencyContact(val params: EmergencyContactParams) : MedicalRecordEvent()
    data class EditEmergencyContact(val id: String, val params: EmergencyContactParams) : MedicalRecordEvent()
    data class DeleteEmergencyContact(val id: String) : MedicalRecordEvent()

    // Navigation Intents
    data class AnalysisClicked(val analysisId: String) : MedicalRecordEvent()
    object AddRecordClicked : MedicalRecordEvent()
}

// --- SIDE EFFECTS (One-off events) ---
sealed class MedicalRecordEffect {
    data class ShowError(val message: String) : MedicalRecordEffect()
    data class NavigateToAnalysisDetails(val analysisId: String) : MedicalRecordEffect()
    object NavigateToAddRecord : MedicalRecordEffect()
    data class ShowSnackbar(val message: String) : MedicalRecordEffect()
}

class MedicalRecordViewModel(
    private val patientRepository: PatientRepository,
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
    private val deleteEmergencyContactUseCase: DeleteEmergencyContactUseCase,
    private val sessionManager: UserSessionManager
) : ScreenModel {
    private var currentPatientId = ""

    private val _state = MutableStateFlow(MedicalRecordState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<MedicalRecordEffect>()
    val effect = _effect.asSharedFlow()

    init {
        screenModelScope.launch {
            val id = sessionManager.getUserId()
            if (id != null) {
                currentPatientId = id
                onEvent(MedicalRecordEvent.LoadFullRecords)
            } else {
                _state.update { it.copy(error = "User session expired") }
            }
        }
    }

    fun onEvent(event: MedicalRecordEvent) {
        when (event) {
            MedicalRecordEvent.LoadFullRecords -> screenModelScope.launch { loadAllData() }
            MedicalRecordEvent.Refresh -> screenModelScope.launch { loadAllData() }
            is MedicalRecordEvent.UpdatePatientInfo -> updatePatientInfo(event.params)

            // Allergies
            is MedicalRecordEvent.AddAllergy -> addAllergy(event.params)
            is MedicalRecordEvent.EditAllergy -> editAllergy(event.id, event.params)
            is MedicalRecordEvent.DeleteAllergy -> deleteAllergy(event.id)

            // Chronic Diseases
            is MedicalRecordEvent.AddChronicDisease -> addChronicDisease(event.params)
            is MedicalRecordEvent.EditChronicDisease -> editChronicDisease(event.id, event.params)
            is MedicalRecordEvent.DeleteChronicDisease -> deleteChronicDisease(event.id)

            // Surgeries
            is MedicalRecordEvent.AddSurgery -> addSurgery(event.params)
            is MedicalRecordEvent.EditSurgery -> editSurgery(event.id, event.params)
            is MedicalRecordEvent.DeleteSurgery -> deleteSurgery(event.id)

            // Family History
            is MedicalRecordEvent.AddFamilyHistory -> addFamilyHistory(event.params)
            is MedicalRecordEvent.EditFamilyHistory -> editFamilyHistory(event.id, event.params)
            is MedicalRecordEvent.DeleteFamilyHistory -> deleteFamilyHistory(event.id)

            // Emergency Contacts
            is MedicalRecordEvent.AddEmergencyContact -> addEmergencyContact(event.params)
            is MedicalRecordEvent.EditEmergencyContact -> editEmergencyContact(event.id, event.params)
            is MedicalRecordEvent.DeleteEmergencyContact -> deleteEmergencyContact(event.id)

            is MedicalRecordEvent.AnalysisClicked -> {
                screenModelScope.launch {
                    _effect.emit(MedicalRecordEffect.NavigateToAnalysisDetails(event.analysisId))
                }
            }

            MedicalRecordEvent.AddRecordClicked -> {
                screenModelScope.launch {
                     _effect.emit(MedicalRecordEffect.NavigateToAddRecord)
                }
            }
        }
    }

    private suspend fun loadAllData(silent: Boolean = false) {
        if (!silent) _state.update { it.copy(isLoading = true, error = null) }

        val profileResult = patientRepository.getPatientById(currentPatientId)

        if (profileResult.isSuccess) {
            val patient = profileResult.getOrNull()
            _state.update {
                it.copy(
                    isLoading = false,
                    patientProfile = patient,
                    allergies = patient?.allergies ?: emptyList(),
                )
            }
        } else {
            _state.update {
                it.copy(
                    isLoading = false,
                    error = profileResult.exceptionOrNull()?.message ?: "Failed to load profile"
                )
            }
        }
    }

    private fun updatePatientInfo(params: UpdatePatientParams) {
        screenModelScope.launch {
            _state.update { it.copy(isUpdatingBasicInfo = true) }
            patientRepository.updatePatient(currentPatientId, params).fold(
                onSuccess = {
                    loadAllData(silent = true)
                    _state.update { it.copy(isUpdatingBasicInfo = false) }
                    _effect.emit(MedicalRecordEffect.ShowSnackbar("Profile updated"))
                },
                onFailure = { error ->
                    _state.update { it.copy(isUpdatingBasicInfo = false) }
                    _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update: ${error.message}"))
                }
            )
        }
    }

    // --- Mutation Handlers (Similar to Doctor ViewModel but using currentPatientId) ---

    private fun addAllergy(params: AllergyParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            addAllergyUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Allergy added"))
                _state.update { it.copy(isProcessingAllergy = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingAllergy = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to add allergy"))
            }
        }
    }

    private fun editAllergy(id: String, params: AllergyParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            updateAllergyUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Allergy updated"))
                _state.update { it.copy(isProcessingAllergy = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingAllergy = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update allergy"))
            }
        }
    }

    private fun deleteAllergy(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            deleteAllergyUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Allergy deleted"))
                _state.update { it.copy(isProcessingAllergy = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingAllergy = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to delete allergy"))
            }
        }
    }

    private fun addChronicDisease(params: ChronicDiseaseParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            addChronicDiseaseUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Chronic disease added"))
                _state.update { it.copy(isProcessingDisease = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingDisease = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to add chronic disease"))
            }
        }
    }

    private fun editChronicDisease(id: String, params: ChronicDiseaseParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            updateChronicDiseaseUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Chronic disease updated"))
                _state.update { it.copy(isProcessingDisease = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingDisease = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update chronic disease"))
            }
        }
    }

    private fun deleteChronicDisease(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            deleteChronicDiseaseUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Chronic disease deleted"))
                _state.update { it.copy(isProcessingDisease = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingDisease = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to delete chronic disease"))
            }
        }
    }

    private fun addSurgery(params: SurgeryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            addSurgeryUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Surgery added"))
                _state.update { it.copy(isProcessingSurgery = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingSurgery = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to add surgery"))
            }
        }
    }

    private fun editSurgery(id: String, params: SurgeryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            updateSurgeryUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Surgery updated"))
                _state.update { it.copy(isProcessingSurgery = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingSurgery = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update surgery"))
            }
        }
    }

    private fun deleteSurgery(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            deleteSurgeryUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Surgery deleted"))
                _state.update { it.copy(isProcessingSurgery = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingSurgery = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to delete surgery"))
            }
        }
    }

    private fun addFamilyHistory(params: FamilyHistoryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            addFamilyHistoryUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Family history added"))
                _state.update { it.copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingFamilyHistory = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to add family history"))
            }
        }
    }

    private fun editFamilyHistory(id: String, params: FamilyHistoryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            updateFamilyHistoryUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Family history updated"))
                _state.update { it.copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingFamilyHistory = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update family history"))
            }
        }
    }

    private fun deleteFamilyHistory(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            deleteFamilyHistoryUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Family history deleted"))
                _state.update { it.copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingFamilyHistory = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to delete family history"))
            }
        }
    }

    private fun addEmergencyContact(params: EmergencyContactParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            addEmergencyContactUseCase(currentPatientId, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Emergency contact added"))
                _state.update { it.copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingEmergencyContact = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to add emergency contact"))
            }
        }
    }

    private fun editEmergencyContact(id: String, params: EmergencyContactParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            updateEmergencyContactUseCase(currentPatientId, id, params).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Emergency contact updated"))
                _state.update { it.copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingEmergencyContact = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to update emergency contact"))
            }
        }
    }

    private fun deleteEmergencyContact(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            deleteEmergencyContactUseCase(currentPatientId, id).onSuccess {
                loadAllData(silent = true)
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Emergency contact deleted"))
                _state.update { it.copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingEmergencyContact = false) }
                _effect.emit(MedicalRecordEffect.ShowSnackbar("Failed to delete emergency contact"))
            }
        }
    }
}
