package org.example.project.presentation.doctor.records

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import org.example.project.domain.model.auth.*
import org.example.project.domain.model.appointment.*
import org.example.project.domain.model.chat.*
import org.example.project.domain.model.doctor.*
import org.example.project.domain.model.home.*
import org.example.project.domain.model.medical_record.*
import org.example.project.domain.model.notification.*
import org.example.project.domain.model.patient.*
import org.example.project.domain.model.specialty.*
import org.example.project.domain.usecase.auth.*
import org.example.project.domain.usecase.appointment.*
import org.example.project.domain.usecase.chat.*
import org.example.project.domain.usecase.doctor.*
import org.example.project.domain.usecase.home.*
import org.example.project.domain.usecase.medical_record.*
import org.example.project.domain.usecase.notification.*
import org.example.project.domain.usecase.patient.*
import org.example.project.domain.usecase.specialty.*

data class DoctorPatientRecordsState(
    val isLoading: Boolean = false,
    val patientProfile: Patient? = null,
    val error: String? = null,
    val isUpdatingBasicInfo: Boolean = false,
    val isProcessingAllergy: Boolean = false,
    val isProcessingDisease: Boolean = false,
    val isProcessingSurgery: Boolean = false,
    val isProcessingFamilyHistory: Boolean = false,
    val isProcessingEmergencyContact: Boolean = false
)

sealed class DoctorPatientRecordsEvent {
    object LoadPatientData : DoctorPatientRecordsEvent()
    data class UpdateBasicInfo(val weight: Double, val height: Double) : DoctorPatientRecordsEvent()

    // Allergies
    data class AddAllergy(val params: AllergyParams) : DoctorPatientRecordsEvent()
    data class EditAllergy(val id: String, val params: AllergyParams) : DoctorPatientRecordsEvent()
    data class DeleteAllergy(val id: String) : DoctorPatientRecordsEvent()

    // Chronic Diseases
    data class AddChronicDisease(val params: ChronicDiseaseParams) : DoctorPatientRecordsEvent()
    data class EditChronicDisease(val id: String, val params: ChronicDiseaseParams) : DoctorPatientRecordsEvent()
    data class DeleteChronicDisease(val id: String) : DoctorPatientRecordsEvent()

    // Surgeries
    data class AddSurgery(val params: SurgeryParams) : DoctorPatientRecordsEvent()
    data class EditSurgery(val id: String, val params: SurgeryParams) : DoctorPatientRecordsEvent()
    data class DeleteSurgery(val id: String) : DoctorPatientRecordsEvent()

    // Family History
    data class AddFamilyHistory(val params: FamilyHistoryParams) : DoctorPatientRecordsEvent()
    data class EditFamilyHistory(val id: String, val params: FamilyHistoryParams) : DoctorPatientRecordsEvent()
    data class DeleteFamilyHistory(val id: String) : DoctorPatientRecordsEvent()

    // Emergency Contact
    data class AddEmergencyContact(val params: EmergencyContactParams) : DoctorPatientRecordsEvent()
    data class EditEmergencyContact(val id: String, val params: EmergencyContactParams) : DoctorPatientRecordsEvent()
    data class DeleteEmergencyContact(val id: String) : DoctorPatientRecordsEvent()
}

sealed class DoctorPatientRecordsEffect {
    data class ShowSnackbar(val message: String) : DoctorPatientRecordsEffect()
}

class DoctorPatientRecordsViewModel(
    private val patientId: String,
    private val getPatientByIdUseCase: GetPatientByIdUseCase,
    private val updatePatientUseCase: UpdatePatientUseCase,
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
) : ScreenModel {

    private val _state = MutableStateFlow(DoctorPatientRecordsState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<DoctorPatientRecordsEffect>()
    val effect = _effect.asSharedFlow()

    init {
        onEvent(DoctorPatientRecordsEvent.LoadPatientData)
    }

    fun onEvent(event: DoctorPatientRecordsEvent) {
        when (event) {
            is DoctorPatientRecordsEvent.LoadPatientData -> loadPatientData()
            is DoctorPatientRecordsEvent.UpdateBasicInfo -> updateBasicInfo(event.weight, event.height)

            // Allergies
            is DoctorPatientRecordsEvent.AddAllergy -> addAllergy(event.params)
            is DoctorPatientRecordsEvent.EditAllergy -> editAllergy(event.id, event.params)
            is DoctorPatientRecordsEvent.DeleteAllergy -> deleteAllergy(event.id)

            // Chronic Diseases
            is DoctorPatientRecordsEvent.AddChronicDisease -> addChronicDisease(event.params)
            is DoctorPatientRecordsEvent.EditChronicDisease -> editChronicDisease(event.id, event.params)
            is DoctorPatientRecordsEvent.DeleteChronicDisease -> deleteChronicDisease(event.id)

            // Surgeries
            is DoctorPatientRecordsEvent.AddSurgery -> addSurgery(event.params)
            is DoctorPatientRecordsEvent.EditSurgery -> editSurgery(event.id, event.params)
            is DoctorPatientRecordsEvent.DeleteSurgery -> deleteSurgery(event.id)

            // Family History
            is DoctorPatientRecordsEvent.AddFamilyHistory -> addFamilyHistory(event.params)
            is DoctorPatientRecordsEvent.EditFamilyHistory -> editFamilyHistory(event.id, event.params)
            is DoctorPatientRecordsEvent.DeleteFamilyHistory -> deleteFamilyHistory(event.id)

            // Emergency Contacts
            is DoctorPatientRecordsEvent.AddEmergencyContact -> addEmergencyContact(event.params)
            is DoctorPatientRecordsEvent.EditEmergencyContact -> editEmergencyContact(event.id, event.params)
            is DoctorPatientRecordsEvent.DeleteEmergencyContact -> deleteEmergencyContact(event.id)
        }
    }

    private fun loadPatientData() {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getPatientByIdUseCase(patientId).onSuccess { patient ->
                _state.update { it.copy(isLoading = false, patientProfile = patient) }
            }.onFailure { error ->
                _state.update { it.copy(isLoading = false, error = error.message) }
            }
        }
    }

    private fun updateBasicInfo(weight: Double, height: Double) {
        screenModelScope.launch {
            _state.update { it.copy(isUpdatingBasicInfo = true) }
            updatePatientUseCase(patientId, UpdatePatientParams(weight = weight, height = height))
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Basic info updated"))
                    _state.update { it.copy(isUpdatingBasicInfo = false) }
                }.onFailure {
                    _state.update { it.copy(isUpdatingBasicInfo = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update basic info"))
                }
        }
    }

    // --- Allergy Handlers ---
    private fun addAllergy(params: AllergyParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            addAllergyUseCase(patientId, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Allergy added"))
                    _state.update { it.copy(isProcessingAllergy = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingAllergy = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to add allergy"))
                }
        }
    }

    private fun editAllergy(id: String, params: AllergyParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            updateAllergyUseCase(patientId, id, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Allergy updated"))
                    _state.update { it.copy(isProcessingAllergy = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingAllergy = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update allergy"))
                }
        }
    }

    private fun deleteAllergy(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingAllergy = true) }
            deleteAllergyUseCase(patientId, id).onSuccess {
                loadPatientData()
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Allergy deleted"))
                _state.update { it.copy(isProcessingAllergy = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingAllergy = false) }
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to delete allergy"))
            }
        }
    }

    // --- Chronic Disease Handlers ---
    private fun addChronicDisease(params: ChronicDiseaseParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            addChronicDiseaseUseCase(patientId, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Chronic disease added"))
                    _state.update { it.copy(isProcessingDisease = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingDisease = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to add chronic disease"))
                }
        }
    }

    private fun editChronicDisease(id: String, params: ChronicDiseaseParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            updateChronicDiseaseUseCase(patientId, id, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Chronic disease updated"))
                    _state.update { it.copy(isProcessingDisease = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingDisease = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update chronic disease"))
                }
        }
    }

    private fun deleteChronicDisease(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingDisease = true) }
            deleteChronicDiseaseUseCase(patientId, id).onSuccess {
                loadPatientData()
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Chronic disease deleted"))
                _state.update { it.copy(isProcessingDisease = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingDisease = false) }
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to delete chronic disease"))
            }
        }
    }

    // --- Surgery Handlers ---
    private fun addSurgery(params: SurgeryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            addSurgeryUseCase(patientId, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Surgery added"))
                    _state.update { it.copy(isProcessingSurgery = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingSurgery = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to add surgery"))
                }
        }
    }

    private fun editSurgery(id: String, params: SurgeryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            updateSurgeryUseCase(patientId, id, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Surgery updated"))
                    _state.update { it.copy(isProcessingSurgery = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingSurgery = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update surgery"))
                }
        }
    }

    private fun deleteSurgery(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingSurgery = true) }
            deleteSurgeryUseCase(patientId, id).onSuccess {
                loadPatientData()
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Surgery deleted"))
                _state.update { it.copy(isProcessingSurgery = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingSurgery = false) }
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to delete surgery"))
            }
        }
    }

    // --- Family History Handlers ---
    private fun addFamilyHistory(params: FamilyHistoryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            addFamilyHistoryUseCase(patientId, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Family history added"))
                    _state.update { it.copy(isProcessingFamilyHistory = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingFamilyHistory = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to add family history"))
                }
        }
    }

    private fun editFamilyHistory(id: String, params: FamilyHistoryParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            updateFamilyHistoryUseCase(patientId, id, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Family history updated"))
                    _state.update { it.copy(isProcessingFamilyHistory = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingFamilyHistory = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update family history"))
                }
        }
    }

    private fun deleteFamilyHistory(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingFamilyHistory = true) }
            deleteFamilyHistoryUseCase(patientId, id).onSuccess {
                loadPatientData()
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Family history deleted"))
                _state.update { it.copy(isProcessingFamilyHistory = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingFamilyHistory = false) }
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to delete family history"))
            }
        }
    }

    // --- Emergency Contact Handlers ---
    private fun addEmergencyContact(params: EmergencyContactParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            addEmergencyContactUseCase(patientId, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Emergency contact added"))
                    _state.update { it.copy(isProcessingEmergencyContact = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingEmergencyContact = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to add emergency contact"))
                }
        }
    }

    private fun editEmergencyContact(id: String, params: EmergencyContactParams) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            updateEmergencyContactUseCase(patientId, id, params)
                .onSuccess {
                    loadPatientData()
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Emergency contact updated"))
                    _state.update { it.copy(isProcessingEmergencyContact = false) }
                }.onFailure {
                    _state.update { it.copy(isProcessingEmergencyContact = false) }
                    _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to update emergency contact"))
                }
        }
    }

    private fun deleteEmergencyContact(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isProcessingEmergencyContact = true) }
            deleteEmergencyContactUseCase(patientId, id).onSuccess {
                loadPatientData()
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Emergency contact deleted"))
                _state.update { it.copy(isProcessingEmergencyContact = false) }
            }.onFailure {
                _state.update { it.copy(isProcessingEmergencyContact = false) }
                _effect.emit(DoctorPatientRecordsEffect.ShowSnackbar("Failed to delete emergency contact"))
            }
        }
    }
}
