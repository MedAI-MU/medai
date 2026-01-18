package org.example.project.presentation.bookingScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.domain.usecase.BookAppointmentUseCase
import org.example.project.domain.usecase.GetAvailableSlotsUseCase
import org.example.project.domain.usecase.GetDoctorDetailsUseCase
import org.example.project.presentation.homeScreen.CalendarUiModel

class BookingViewModel(
    private val doctorId: String,
    private val getDoctorDetailsUseCase: GetDoctorDetailsUseCase,
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val bookAppointmentUseCase: BookAppointmentUseCase,
    private val calendarManager: CalendarManager
) : ScreenModel {

    private val _state = MutableStateFlow(BookingState())
    val state = _state.asStateFlow()

    private val _effect = Channel<BookingEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        loadDoctor()
        initCalendar()
    }

    private fun loadDoctor() {
        screenModelScope.launch {
            val result = getDoctorDetailsUseCase(doctorId)
            result.fold(
                onSuccess = { doc -> _state.update { it.copy(doctor = doc, isLoadingDoctor = false) } },
                onFailure = { /* Handle error */ }
            )
        }
    }

    private fun initCalendar() {
        val today = calendarManager.getToday()
        _state.update {
            it.copy(
                selectedDate = today,
                displayedMonth = today
            )
        }
        generateCalendar(today)
        loadSlots(today)
    }

    private fun generateCalendar(baseDate: LocalDate) {
        val days = calendarManager.getDaysForMonth(baseDate)
        val selected = _state.value.selectedDate
        val uiDays = days.map { date ->
            CalendarUiModel(
                day = date.dayOfMonth.toString(),
                weekDay = date.dayOfWeek.name.take(3),
                fullDate = date,
                isSelected = date == selected
            )
        }
        _state.update { it.copy(calendarDays = uiDays) }
    }

    private fun changeDisplayedMonth(newMonthBase: LocalDate) {
        _state.update { it.copy(displayedMonth = newMonthBase) }
        generateCalendar(newMonthBase)
    }
    private fun loadSlots(date: LocalDate) {
        screenModelScope.launch {
            _state.update { it.copy(isLoadingSlots = true) }
            val result = getAvailableSlotsUseCase(doctorId, date)
            result.fold(
                onSuccess = { slots -> _state.update { it.copy(slots = slots, isLoadingSlots = false) } },
                onFailure = { _state.update { it.copy(isLoadingSlots = false) } }
            )
        }
    }

    fun onEvent(event: BookingEvent) {
        when(event) {
            BookingEvent.BackClicked -> sendEffect(BookingEffect.NavigateBack)
            is BookingEvent.DateSelected -> {
                if (_state.value.selectedDate != event.date) {
                    _state.update { it.copy(selectedDate = event.date) }

                    val displayMonth = _state.value.displayedMonth ?: event.date
                    generateCalendar(displayMonth)

                    loadSlots(event.date)
                }
            }
            is BookingEvent.SlotSelected -> {
                _state.update { it.copy(selectedSlotId = event.slotId) }
            }
            is BookingEvent.PatientTypeChanged -> {
                _state.update { it.copy(bookingForSelf = event.isSelf) }
            }
            is BookingEvent.ProblemDescChanged -> {
                _state.update { it.copy(problemDescription = event.text) }
            }
            BookingEvent.BookClicked -> {
                performBooking()
            }
            is BookingEvent.PatientNameChanges -> { _state.update { it.copy(patientName = event.name) } }
            is BookingEvent.PatientAgeChanged -> _state.update { it.copy(patientAge = event.age) }
            is BookingEvent.PatientGenderChanged -> _state.update { it.copy(patientGender = event.gender) }
            BookingEvent.NextMonthClicked -> {
                val current = _state.value.displayedMonth ?: return
                val newMonth = calendarManager.getNextMonth(current)
                changeDisplayedMonth(newMonth)
            }
            BookingEvent.PrevMonthClicked -> {
                val current = _state.value.displayedMonth ?: return
                val newMonth = calendarManager.getPreviousMonth(current)
                changeDisplayedMonth(newMonth)
            }

        }
    }

    private fun performBooking() {
        val currentState = _state.value

        if (currentState.selectedSlotId == null) {
            sendEffect(BookingEffect.ShowError("Please select a time slot"))
            return
        }
        if (currentState.patientName.isBlank() || currentState.problemDescription.isBlank()) {
            sendEffect(BookingEffect.ShowError("Please fill in all details"))
            return
        }
        // Safety check for date
        val date = currentState.selectedDate ?: return

        screenModelScope.launch {
            _state.update { it.copy(isBooking = true) }

            val result = bookAppointmentUseCase(
                doctorId = doctorId,
                slotId = currentState.selectedSlotId,
                date = date,
                patientName = currentState.patientName,
                patientAge = currentState.patientAge,
                patientGender = currentState.patientGender,
                problemDescription = currentState.problemDescription
            )

            result.fold(
                onSuccess = {
                    _state.update { it.copy(isBooking = false) }
                    sendEffect(BookingEffect.NavigateToSuccess)
                },
                onFailure = { error ->
                    _state.update { it.copy(isBooking = false) }
                    sendEffect(BookingEffect.ShowError(error.message ?: "Booking Failed"))
                }
            )
        }
    }

    private fun sendEffect(effect: BookingEffect) {
        screenModelScope.launch { _effect.send(effect) }
    }
}
