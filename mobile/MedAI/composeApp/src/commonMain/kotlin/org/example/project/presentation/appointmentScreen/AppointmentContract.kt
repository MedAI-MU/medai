package org.example.project.presentation.appointmentScreen

import org.example.project.domain.model.AppointmentDetail
import org.example.project.domain.model.AppointmentDetailStatus
import org.example.project.domain.model.CancelReason

data class AppointmentState(
    val isLoading: Boolean = false,
    val selectedTab: AppointmentDetailStatus = AppointmentDetailStatus.UPCOMING,
    val appointments: List<AppointmentDetail> = emptyList(),
    val error: String? = null,

    val selectedAppointment: AppointmentDetail? = null,

    val cancelReasons: List<CancelReason> = emptyList(),
    val isCancelling: Boolean = false,

    val isSubmittingReview: Boolean = false
)

sealed interface AppointmentEvent {
    data class OnTabSelected(val status: AppointmentDetailStatus) : AppointmentEvent
    data class OnAppointmentClicked(val id: String) : AppointmentEvent

    // Cancel Flow
    data class OnCancelClicked(val appointmentId: String) : AppointmentEvent
    data class OnConfirmCancel(val appointmentId: String, val reasonId: String, val otherReason: String?) : AppointmentEvent

    // Review Flow
    data class OnReviewClicked(val appointmentId: String) : AppointmentEvent
    data class OnSubmitReview(val appointmentId: String, val rating: Int, val comment: String) : AppointmentEvent

    object Refresh : AppointmentEvent
    object ClearError : AppointmentEvent
}

sealed interface AppointmentEffect {
    data class NavigateToDetails(val id: String) : AppointmentEffect
    object NavigateBack : AppointmentEffect
    data class ShowToast(val message: String) : AppointmentEffect
    object CloseSheet : AppointmentEffect
}
