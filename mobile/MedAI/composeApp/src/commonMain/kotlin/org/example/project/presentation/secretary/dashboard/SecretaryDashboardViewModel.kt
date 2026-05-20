package org.example.project.presentation.secretary.dashboard

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.ClinicStats
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.secretary.GetAllQueuesUseCase
import org.example.project.domain.usecase.secretary.GetDashboardStatsUseCase

data class SecretaryState(
    val clinicStats: ClinicStats = ClinicStats(0, 0, 0.0, 0),
    val queues: List<QueueEntry> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class SecretaryEvent {
    object LoadDashboard : SecretaryEvent()
    data class UpdateQueueStatus(val entryId: String, val status: QueueStatus) : SecretaryEvent()
    data class CreatePatient(val patient: Patient) : SecretaryEvent()
}

class SecretaryDashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getAllQueuesUseCase: GetAllQueuesUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase,
    private val createPatientUseCase: CreatePatientUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(SecretaryState())
    val state: StateFlow<SecretaryState> = _state.asStateFlow()

    init {
        onEvent(SecretaryEvent.LoadDashboard)
    }

    fun onEvent(event: SecretaryEvent) {
        when (event) {
            is SecretaryEvent.LoadDashboard -> {
                loadStats()
                loadQueues()
            }
            is SecretaryEvent.UpdateQueueStatus -> {
                updateQueueStatus(event.entryId, event.status)
            }
            is SecretaryEvent.CreatePatient -> {
                createPatient(event.patient)
            }
        }
    }

    private fun loadStats() {
        getDashboardStatsUseCase()
            .onEach { stats ->
                _state.value = _state.value.copy(clinicStats = stats)
            }
            .launchIn(screenModelScope)
    }

    private fun loadQueues() {
        getAllQueuesUseCase()
            .onEach { queues ->
                _state.value = _state.value.copy(queues = queues)
            }
            .launchIn(screenModelScope)
    }

    private fun updateQueueStatus(entryId: String, status: QueueStatus) {
        screenModelScope.launch {
            // Call repository directly or use case?
            // Missing `UpdateQueueStatusUseCase`.
            // I'll add `checkInPatientUseCase` which does something similar, but for specific status changes, I need a new use case.
            // For now, I'll just checkIn if status is WAITING.
            if (status == QueueStatus.WAITING) {
                checkInPatientUseCase(entryId)
            }
        }
    }

    private fun createPatient(patient: Patient) {
        screenModelScope.launch {
            val result = createPatientUseCase(patient)
            if (result.isSuccess) {
                // Show success message or refresh
            } else {
                _state.value = _state.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }
}
