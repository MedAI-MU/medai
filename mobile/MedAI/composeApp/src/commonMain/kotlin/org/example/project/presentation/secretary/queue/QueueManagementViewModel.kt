package org.example.project.presentation.secretary.queue

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.usecase.secretary.CheckInPatientUseCase
import org.example.project.domain.usecase.secretary.GetAllQueuesUseCase

class QueueManagementViewModel(
    private val getAllQueuesUseCase: GetAllQueuesUseCase,
    private val checkInPatientUseCase: CheckInPatientUseCase
) : MviScreenModel<QueueManagementState, QueueManagementEvent, QueueManagementEffect>(QueueManagementState()) {

    init {
        onEvent(QueueManagementEvent.LoadQueues)
    }

    override fun onEvent(event: QueueManagementEvent) {
        when (event) {
            is QueueManagementEvent.LoadQueues -> loadQueues()
            is QueueManagementEvent.CheckIn -> checkIn(event.appointmentId)
        }
    }

    private fun loadQueues() {
        setState { copy(isLoading = true) }
        getAllQueuesUseCase()
            .onEach { queues ->
                setState { copy(queues = queues, isLoading = false) }
            }
            .launchIn(screenModelScope)
    }

    private fun checkIn(appointmentId: String) {
        screenModelScope.launch {
            try {
                checkInPatientUseCase(appointmentId)
                sendEffect(QueueManagementEffect.ShowSnackbar("Patient checked in successfully"))
            } catch (e: Exception) {
                sendEffect(QueueManagementEffect.ShowSnackbar(e.message ?: "Failed to check in patient", isError = true))
            }
        }
    }
}
