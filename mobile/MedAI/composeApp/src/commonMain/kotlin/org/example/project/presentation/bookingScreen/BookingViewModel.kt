package org.example.project.presentation.bookingScreen

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.domain.usecase.appointment.BookAppointmentUseCase
import org.example.project.domain.usecase.appointment.GetAvailableSlotsUseCase
import org.example.project.domain.usecase.doctor.GetDoctorDetailsUseCase
import org.example.project.domain.usecase.profile.GetProfileUseCase
import org.example.project.presentation.homeScreen.CalendarUiModel

class BookingViewModel(
    private val doctorId: String,
    private val getDoctorDetailsUseCase: GetDoctorDetailsUseCase,
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val bookAppointmentUseCase: BookAppointmentUseCase,
    private val calendarManager: CalendarManager,
    private val getProfileUseCase: GetProfileUseCase
) : MviScreenModel<BookingState, BookingEvent, BookingEffect>(BookingState()) {

    init {
        loadDoctor()
        initCalendar()
        loadPatientProfile()
    }

    private fun loadDoctor() {
        screenModelScope.launch {
            val result = getDoctorDetailsUseCase(doctorId)
            result.fold(
                onSuccess = { doc -> setState { copy(doctor = doc, isLoadingDoctor = false) } },
                onFailure = { err ->
                    setState { copy(isLoadingDoctor = false) }
                    sendEffect(BookingEffect.ShowError(err.message ?: "Failed to load doctor profile"))
                }
            )
        }
    }

    private fun loadPatientProfile() {
        screenModelScope.launch {
            getProfileUseCase().fold(
                onSuccess = { user ->
                    setState {
                        copy(
                            loggedInPatientName = user.name,
                            patientName = if (bookingForSelf) user.name else patientName
                        )
                    }
                },
                onFailure = { err ->
                    sendEffect(BookingEffect.ShowError(err.message ?: "Failed to load patient profile"))
                }
            )
        }
    }

    private fun initCalendar() {
        val today = calendarManager.getToday()
        setState {
            copy(
                selectedDate = today,
                displayedMonth = today
            )
        }
        generateCalendar(today)
        loadSlots(today)
    }

    private fun generateCalendar(baseDate: LocalDate) {
        val days = calendarManager.getDaysForMonth(baseDate)
        val selected = state.value.selectedDate
        val today = calendarManager.getToday()
        val uiDays = days.map { date ->
            CalendarUiModel(
                day = date.dayOfMonth.toString(),
                weekDay = date.dayOfWeek.name.take(3),
                fullDate = date,
                isSelected = date == selected,
                isEnabled = date >= today
            )
        }
        setState { copy(calendarDays = uiDays) }
    }

    private fun changeDisplayedMonth(newMonthBase: LocalDate) {
        setState { copy(displayedMonth = newMonthBase) }
        generateCalendar(newMonthBase)
    }

    private fun loadSlots(date: LocalDate) {
        screenModelScope.launch {
            setState { copy(isLoadingSlots = true) }
            val result = getAvailableSlotsUseCase(doctorId, date)
            result.fold(
                onSuccess = { slots -> setState { copy(slots = slots, isLoadingSlots = false) } },
                onFailure = { setState { copy(isLoadingSlots = false) } }
            )
        }
    }

    override fun onEvent(event: BookingEvent) {
        when(event) {
            BookingEvent.BackClicked -> sendEffect(BookingEffect.NavigateBack)
            is BookingEvent.DateSelected -> {
                if (state.value.selectedDate != event.date) {
                    setState { copy(selectedDate = event.date) }

                    val displayMonth = state.value.displayedMonth ?: event.date
                    generateCalendar(displayMonth)

                    loadSlots(event.date)
                }
            }
            is BookingEvent.SlotSelected -> {
                setState { copy(selectedSlotId = event.slotId) }
            }
            is BookingEvent.PatientTypeChanged -> {
                setState {
                    copy(
                        bookingForSelf = event.isSelf,
                        patientName = if (event.isSelf) loggedInPatientName else ""
                    )
                }
            }
            BookingEvent.BookClicked -> {
                performBooking()
            }
            is BookingEvent.PatientNameChanges -> {
                setState { copy(patientName = event.name) }
            }
            BookingEvent.NextMonthClicked -> {
                val current = state.value.displayedMonth ?: return
                val newMonth = calendarManager.getNextMonth(current)
                changeDisplayedMonth(newMonth)
            }
            BookingEvent.PrevMonthClicked -> {
                val current = state.value.displayedMonth ?: return
                val newMonth = calendarManager.getPreviousMonth(current)
                changeDisplayedMonth(newMonth)
            }
        }
    }

    private fun performBooking() {
        val currentState = state.value

        if (currentState.selectedSlotId == null) {
            sendEffect(BookingEffect.ShowSnackbar("Please select a time slot"))
            return
        }

        val selectedDate = currentState.selectedDate
        if (selectedDate == null) {
            sendEffect(BookingEffect.ShowSnackbar("Please select a date"))
            return
        }
        val today = calendarManager.getToday()
        if (selectedDate < today) {
            sendEffect(BookingEffect.ShowSnackbar("Cannot book an appointment in the past"))
            return
        }

        if (currentState.patientName.isBlank()) {
            sendEffect(BookingEffect.ShowSnackbar("Please enter patient name"))
            return
        }

        screenModelScope.launch {
            setState { copy(isBooking = true) }

            val result = bookAppointmentUseCase(
                doctorId = doctorId,
                slotId = currentState.selectedSlotId
            )

            result.fold(
                onSuccess = {
                    setState { copy(isBooking = false) }
                    sendEffect(BookingEffect.NavigateToSuccess)
                },
                onFailure = { error ->
                    setState { copy(isBooking = false) }
                    sendEffect(BookingEffect.ShowError(error.message ?: "Booking Failed"))
                }
            )
        }
    }
}
