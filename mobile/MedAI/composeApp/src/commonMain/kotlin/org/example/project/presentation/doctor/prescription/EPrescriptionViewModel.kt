package org.example.project.presentation.doctor.prescription

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class EPrescriptionState(
    val isLoading: Boolean = false,
    val patientName: String = "",
    val medicationName: String = "",
    val dosage: String = "",
    val instructions: String = "",
    val error: String? = null
)

sealed class EPrescriptionEvent {
    data class PatientNameChanged(val value: String) : EPrescriptionEvent()
    data class MedicationNameChanged(val value: String) : EPrescriptionEvent()
    data class DosageChanged(val value: String) : EPrescriptionEvent()
    data class InstructionsChanged(val value: String) : EPrescriptionEvent()
    object SubmitClicked : EPrescriptionEvent()
}

sealed class EPrescriptionEffect {
    object NavigateBack : EPrescriptionEffect()
    data class ShowSuccess(val message: String) : EPrescriptionEffect()
    data class ShowError(val message: String) : EPrescriptionEffect()
}

class EPrescriptionViewModel : ScreenModel {

    private val _state = MutableStateFlow(EPrescriptionState())
    val state = _state.asStateFlow()

    private val _effect = Channel<EPrescriptionEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: EPrescriptionEvent) {
        when (event) {
            is EPrescriptionEvent.PatientNameChanged -> _state.update { it.copy(patientName = event.value) }
            is EPrescriptionEvent.MedicationNameChanged -> _state.update { it.copy(medicationName = event.value) }
            is EPrescriptionEvent.DosageChanged -> _state.update { it.copy(dosage = event.value) }
            is EPrescriptionEvent.InstructionsChanged -> _state.update { it.copy(instructions = event.value) }
            EPrescriptionEvent.SubmitClicked -> submitPrescription()
        }
    }

    private fun submitPrescription() {
        val currentState = _state.value
        if (currentState.patientName.isBlank() || currentState.medicationName.isBlank()) {
            sendEffect(EPrescriptionEffect.ShowError("Please fill in required fields"))
            return
        }

        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1500) // Simulate Network Call
            _state.update { it.copy(isLoading = false) }
            sendEffect(EPrescriptionEffect.ShowSuccess("Prescription sent to ${currentState.patientName}"))
            delay(500)
            sendEffect(EPrescriptionEffect.NavigateBack)
        }
    }

    private fun sendEffect(effect: EPrescriptionEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
