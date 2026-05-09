package org.example.project.presentation.bookingScreen

import kotlinx.datetime.LocalDate
import org.example.project.domain.model.Doctor
import org.example.project.domain.model.TimeSlot
import org.example.project.presentation.homeScreen.CalendarUiModel

data class BookingState(
    val doctor: Doctor? = null,
    val isLoadingDoctor: Boolean = true,

    // Calendar
    val calendarDays: List<CalendarUiModel> = emptyList(),
    val selectedDate: LocalDate? = null,
    val displayedMonth: LocalDate? = null,

    // Slots
    val slots: List<TimeSlot> = emptyList(),
    val selectedSlotId: String? = null,
    val isLoadingSlots: Boolean = false,

    // Form
    val bookingForSelf: Boolean = true,
    val patientName: String = "", // Default

    val isBooking: Boolean = false,
    val error: String? = null
)

sealed class BookingEvent {
    object BackClicked : BookingEvent()

    // Calendar
    data class DateSelected(val date: LocalDate) : BookingEvent()
    object NextMonthClicked : BookingEvent()
    object PrevMonthClicked : BookingEvent()

    // Slot
    data class SlotSelected(val slotId: String) : BookingEvent()

    // Form
    data class PatientTypeChanged(val isSelf: Boolean) : BookingEvent()
    data class PatientNameChanges(val name: String) : BookingEvent()
    object BookClicked : BookingEvent()
}

sealed class BookingEffect {
    object NavigateBack : BookingEffect()
    object NavigateToSuccess : BookingEffect()
    data class ShowError(val message: String) : BookingEffect()
    data class ShowSuccessMessage(val message: String) : BookingEffect()
}
