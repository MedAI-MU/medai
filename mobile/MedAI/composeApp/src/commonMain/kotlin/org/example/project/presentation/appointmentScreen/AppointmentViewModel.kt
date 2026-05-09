package org.example.project.presentation.appointmentScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.usecase.CancelAppointmentUseCase
import org.example.project.domain.usecase.GetAppointmentDetailsUseCase
import org.example.project.domain.usecase.GetAppointmentsUseCase
import org.example.project.domain.usecase.GetCancelReasonsUseCase
import org.example.project.domain.usecase.SubmitReviewUseCase

class AppointmentViewModel(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val getAppointmentDetailsUseCase: GetAppointmentDetailsUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val getCancelReasonsUseCase: GetCancelReasonsUseCase
) : ScreenModel {

    private val _state = MutableStateFlow(AppointmentState())
    val state = _state.asStateFlow()

    private val _effect = Channel<AppointmentEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

//    init {
//        loadAppointments(_state.value.selectedTab)
//    }

    fun onEvent(event: AppointmentEvent) {
        when (event) {
            is AppointmentEvent.OnTabSelected -> {
                _state.update { it.copy(selectedTab = event.status) }
                loadAppointments(event.status)
            }
            is AppointmentEvent.OnAppointmentClicked -> {
                loadDetails(event.id)
            }
            is AppointmentEvent.OnCancelClicked -> {
                loadCancelReasons()
            }
            is AppointmentEvent.OnConfirmCancel -> {
                cancelAppointment(event.appointmentId)
            }
            is AppointmentEvent.OnReviewClicked -> {
                _state.update { it.copy(isSubmittingReview = false) }
            }
            is AppointmentEvent.OnSubmitReview -> {
                submitReview(event.appointmentId, event.rating, event.comment)
            }
            AppointmentEvent.Refresh -> {
                loadAppointments(_state.value.selectedTab)
            }
            AppointmentEvent.ClearError -> {
                _state.update { it.copy(error = null) }
            }
        }
    }

    private fun loadAppointments(status: AppointmentDetailStatus) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getAppointmentsUseCase(status).fold(
                onSuccess = { list ->
                    _state.update { it.copy(isLoading = false, appointments = list) }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false, error = err.message) }
                }
            )
        }
    }

    private fun loadDetails(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            getAppointmentDetailsUseCase().fold(
                onSuccess = { details ->
                    val detail = details.find { it.id == id }
                    _state.update { it.copy(isLoading = false, selectedAppointment = detail) }
                },
                onFailure = { err ->
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(AppointmentEffect.ShowToast(err.message ?: "Error"))
                }
            )
        }
    }

    private fun loadCancelReasons() {
        screenModelScope.launch {
            getCancelReasonsUseCase().fold(
                onSuccess = { reasons -> _state.update { it.copy(cancelReasons = reasons) } },
                onFailure = { /* Handle error */ }
            )
        }
    }

    private fun cancelAppointment(id: String) {
        screenModelScope.launch {
            _state.update { it.copy(isCancelling = true) }
            cancelAppointmentUseCase(id).fold(
                onSuccess = {
                    _state.update { it.copy(isCancelling = false) }
                    _effect.send(AppointmentEffect.ShowToast("Appointment Cancelled"))
                    _effect.send(AppointmentEffect.CloseSheet)
                    loadAppointments(_state.value.selectedTab) // Refresh list
                },
                onFailure = {
                    _state.update { it.copy(isCancelling = false) }
                    _effect.send(AppointmentEffect.ShowToast("Failed to cancel"))
                }
            )
        }
    }

    private fun submitReview(id: String, rating: Int, comment: String?) {
        screenModelScope.launch {
            _state.update { it.copy(isSubmittingReview = true) }
            submitReviewUseCase(id, rating, comment).fold(
                onSuccess = {
                    _state.update { it.copy(isSubmittingReview = false) }
                    _effect.send(AppointmentEffect.ShowToast("Review Submitted"))
                    _effect.send(AppointmentEffect.CloseSheet)
                },
                onFailure = {
                    _state.update { it.copy(isSubmittingReview = false) }
                    _effect.send(AppointmentEffect.ShowToast("Failed to submit review"))
                }
            )
        }
    }
}
