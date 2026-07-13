package org.example.project.presentation.appointmentScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.domain.usecase.appointment.CancelAppointmentUseCase
import org.example.project.domain.usecase.appointment.GetAppointmentDetailsUseCase
import org.example.project.domain.usecase.appointment.GetAppointmentsUseCase
import org.example.project.domain.usecase.appointment.GetCancelReasonsUseCase
import org.example.project.domain.usecase.appointment.SubmitReviewUseCase
import org.example.project.domain.usecase.report_analysis.GetAllReportsUseCase
import org.example.project.domain.repository.appointment.AppointmentRepository

class AppointmentViewModel(
    private val getAppointmentsUseCase: GetAppointmentsUseCase,
    private val getAppointmentDetailsUseCase: GetAppointmentDetailsUseCase,
    private val cancelAppointmentUseCase: CancelAppointmentUseCase,
    private val submitReviewUseCase: SubmitReviewUseCase,
    private val getCancelReasonsUseCase: GetCancelReasonsUseCase,
    private val appointmentRepository: AppointmentRepository,
    private val getAllReportsUseCase: GetAllReportsUseCase
) : MviScreenModel<AppointmentState, AppointmentEvent, AppointmentEffect>(AppointmentState()) {

    init {
        loadAppointments(state.value.selectedTab)
        observeRefreshSignals()
    }

    private fun observeRefreshSignals() {
        screenModelScope.launch {
            appointmentRepository.appointmentsRefreshSignals.collect {
                loadAppointments(state.value.selectedTab)
            }
        }
    }

    override fun onEvent(event: AppointmentEvent) {
        when (event) {
            is AppointmentEvent.OnTabSelected -> {
                setState { copy(selectedTab = event.status) }
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
                setState { copy(isSubmittingReview = false) }
            }
            is AppointmentEvent.OnSubmitReview -> {
                submitReview(event.appointmentId, event.rating, event.comment)
            }
            AppointmentEvent.Refresh -> {
                loadAppointments(state.value.selectedTab)
            }
            AppointmentEvent.ClearError -> {
                setState { copy(error = null) }
            }
            is AppointmentEvent.ViewPatientRecords -> {
                sendEffect(AppointmentEffect.NavigateToPatientRecords(event.patientId))
            }
        }
    }

    private fun loadAppointments(status: AppointmentDetailStatus) {
        screenModelScope.launch {
            setState { copy(isLoading = true, error = null) }
            getAppointmentsUseCase(status).fold(
                onSuccess = { list ->
                    setState { copy(isLoading = false, appointments = list) }
                },
                onFailure = { err ->
                    setState { copy(isLoading = false, error = err.message) }
                }
            )
        }
    }

    private fun loadDetails(id: String) {
        screenModelScope.launch {
            setState { copy(isLoading = true) }
            getAppointmentDetailsUseCase().fold(
                onSuccess = { details ->
                    val detail = details.find { it.id == id }
                    getAllReportsUseCase().fold(
                        onSuccess = { reports ->
                            val linked = reports.filter { it.appointmentId == id }
                            setState { copy(isLoading = false, selectedAppointment = detail, linkedReports = linked) }
                        },
                        onFailure = {
                            setState { copy(isLoading = false, selectedAppointment = detail, linkedReports = emptyList()) }
                        }
                    )
                },
                onFailure = { err ->
                    setState { copy(isLoading = false) }
                    sendEffect(AppointmentEffect.ShowToast(err.message ?: "Error"))
                }
            )
        }
    }

    private fun loadCancelReasons() {
        screenModelScope.launch {
            getCancelReasonsUseCase().fold(
                onSuccess = { reasons -> setState { copy(cancelReasons = reasons) } },
                onFailure = { err ->
                    sendEffect(AppointmentEffect.ShowToast(err.message ?: "Failed to load cancel reasons"))
                }
            )
        }
    }

    private fun cancelAppointment(id: String) {
        screenModelScope.launch {
            setState { copy(isCancelling = true) }
            cancelAppointmentUseCase(id).fold(
                onSuccess = {
                    setState { copy(isCancelling = false) }
                    sendEffect(AppointmentEffect.ShowToast("Appointment Cancelled"))
                    sendEffect(AppointmentEffect.CloseSheet)
                    loadAppointments(state.value.selectedTab) // Refresh list
                },
                onFailure = {
                    setState { copy(isCancelling = false) }
                    sendEffect(AppointmentEffect.ShowToast("Failed to cancel"))
                }
            )
        }
    }

    private fun submitReview(id: String, rating: Int, comment: String?) {
        screenModelScope.launch {
            setState { copy(isSubmittingReview = true) }
            submitReviewUseCase(id, rating, comment).fold(
                onSuccess = {
                    setState { copy(isSubmittingReview = false) }
                    sendEffect(AppointmentEffect.ShowToast("Review Submitted"))
                    sendEffect(AppointmentEffect.CloseSheet)
                },
                onFailure = {
                    setState { copy(isSubmittingReview = false) }
                    sendEffect(AppointmentEffect.ShowToast("Failed to submit review"))
                }
            )
        }
    }
}
