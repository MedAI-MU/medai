package org.example.project.presentation.secretary.dashboard

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.patient.Patient
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.CreatePatientUseCase
import org.example.project.domain.usecase.secretary.GetAllQueuesUseCase
import org.example.project.domain.usecase.secretary.GetDashboardStatsUseCase

class SecretaryDashboardViewModel(
    private val getDashboardStatsUseCase: GetDashboardStatsUseCase,
    private val getAllQueuesUseCase: GetAllQueuesUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase,
    private val createPatientUseCase: CreatePatientUseCase
) : MviScreenModel<SecretaryDashboardState, SecretaryDashboardEvent, SecretaryDashboardEffect>(SecretaryDashboardState()) {

    init {
        onEvent(SecretaryDashboardEvent.LoadDashboard)
    }

    override fun onEvent(event: SecretaryDashboardEvent) {
        when (event) {
            is SecretaryDashboardEvent.LoadDashboard -> {
                loadStats()
                loadQueues()
            }
            is SecretaryDashboardEvent.UpdateQueueStatus -> {
                updateQueueStatus(event.entryId, event.status)
            }
            is SecretaryDashboardEvent.CreatePatient -> {
                createPatient(event.patient)
            }
        }
    }

    private fun loadStats() {
        getDashboardStatsUseCase()
            .onEach { stats ->
                setState { copy(clinicStats = stats) }
            }
            .launchIn(screenModelScope)
    }

    private fun loadQueues() {
        getAllQueuesUseCase()
            .onEach { queues ->
                setState { copy(queues = queues) }
            }
            .launchIn(screenModelScope)
    }

    private fun updateQueueStatus(entryId: String, status: QueueStatus) {
        screenModelScope.launch {
            if (status == QueueStatus.WAITING) {
                checkInPatientUseCase(entryId)
            }
        }
    }

    private fun createPatient(patient: Patient) {
        screenModelScope.launch {
            val result = createPatientUseCase(patient)
            if (result.isSuccess) {
                sendEffect(SecretaryDashboardEffect.ShowSnackbar("Patient created successfully"))
            } else {
                setState { copy(error = result.exceptionOrNull()?.message) }
                sendEffect(SecretaryDashboardEffect.ShowSnackbar(result.exceptionOrNull()?.message ?: "Failed to create patient", isError = true))
            }
        }
    }
}
