package org.example.project.presentation.homeScreen

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.example.project.core.domain.ResourceProvider
import org.example.project.core.presentation.util.CalendarManager
import org.example.project.domain.usecase.home.GetHomeDataUseCase
import org.example.project.domain.usecase.profile.GetProfileUseCase
import org.example.project.domain.repository.appointment.AppointmentRepository

class HomeViewModel(
    private val getHomeDataUseCase: GetHomeDataUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val calendarManager: CalendarManager,
    private val resourceProvider: ResourceProvider,
    private val appointmentRepository: AppointmentRepository
) : ScreenModel {

    // 1. UI State (Persistent data)
    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    // 2. Effect Channel (One-time events like navigation)
    private val _effect = Channel<HomeEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    init {
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        _state.update {
            it.copy(
                selectedDate = today,
                displayedMonth = today // Start viewing current month
            )
        }
        loadData()
        observeRefreshSignals()
    }

    private fun observeRefreshSignals() {
        screenModelScope.launch {
            appointmentRepository.appointmentsRefreshSignals.collect {
                loadData()
            }
        }
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.Refresh -> loadData()

            // --- Calendar Navigation ---
            is HomeEvent.PreviousMonthClicked -> {
                val currentDisplay = _state.value.displayedMonth ?: return
                // Subtract 1 month
                val newMonth = currentDisplay.minus(DatePeriod(months = 1))
                changeDisplayedMonth(newMonth)
            }

            is HomeEvent.NextMonthClicked -> {
                val currentDisplay = _state.value.displayedMonth ?: return
                // Add 1 month
                val newMonth = currentDisplay.plus(DatePeriod(months = 1))
                changeDisplayedMonth(newMonth)
            }

            // --- Item Clicks ---
            is HomeEvent.CategoryClicked -> {
                sendEffect(HomeEffect.NavigateToCategory(event.category))
            }
            is HomeEvent.DoctorClicked -> {
                sendEffect(HomeEffect.NavigateToDoctorDetails(event.doctorId))
            }
            is HomeEvent.AppointmentClicked -> {
                sendEffect(HomeEffect.NavigateToAppointmentDetails(event.appointmentId))
            }
            is HomeEvent.SpecialtyClicked -> {
                val specialty = _state.value.specialties.find { it.id == event.specialtyId }

                if (specialty != null) {
                    screenModelScope.launch {
                        // Resolve string resource to actual string
                        val title = resourceProvider.getString(specialty.title)
                        sendEffect(HomeEffect.NavigateToSpecialty(specialty.iconName, title))
                    }
                }
            }

            // --- "See All" Clicks ---
            is HomeEvent.SeeAllCategoriesClicked -> {
                sendEffect(HomeEffect.NavigateToAllCategories)
            }
            is HomeEvent.SeeAllScheduleClicked -> {
                sendEffect(HomeEffect.NavigateToFullSchedule)
            }
            is HomeEvent.SeeAllSpecialtiesClicked -> {
                sendEffect(HomeEffect.NavigateToAllSpecialties)
            }

            is HomeEvent.DateSelected -> {
                _state.update { it.copy(selectedDate = event.date) }
                // Regenerate calendar UI to update 'isSelected' visuals
                generateCalendarDates(_state.value.displayedMonth ?: event.date)
                filterAppointments(event.date)
            }

            // --- Top Bar Actions ---
            is HomeEvent.NotificationsClicked -> {
                sendEffect(HomeEffect.NavigateToNotifications)
            }
            is HomeEvent.SettingsClicked -> {
                sendEffect(HomeEffect.NavigateToSettings)
            }
            is HomeEvent.SearchClicked -> {
                sendEffect(HomeEffect.NavigateToSearch)
            }
        }
    }

    private fun changeDisplayedMonth(newMonthBase: LocalDate) {
        _state.update { it.copy(displayedMonth = newMonthBase) }
        generateCalendarDates(newMonthBase)
    }

    private fun generateCalendarDates(baseDate: LocalDate) {
        val selectedDate = _state.value.selectedDate
        val appointments = _state.value.allAppointments

        val days = calendarManager.getDaysForMonth(baseDate)

        // Map to UI Model
        val calendarUiList = days.map { date ->
            val hasAppt = appointments.any { it.date == date }

            CalendarUiModel(
                day = date.dayOfMonth.toString(),
                weekDay = date.dayOfWeek.name.take(3),
                fullDate = date,
                isSelected = date == selectedDate,
                hasAppointment = hasAppt
            )
        }

        _state.update { it.copy(calendarDays = calendarUiList) }
    }
    private fun loadData() {
        screenModelScope.launch {
            // Only show full loading spinner if we have no data yet
            // If refreshing, we might just show a pull-to-refresh indicator (handled by UI)
            if (_state.value.categories.isEmpty()) {
                _state.update { it.copy(isLoading = true, error = null) }
            }

            val result = getHomeDataUseCase()

            result.fold(
                onSuccess = { data ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            userName = data.userName,
                            categories = data.categories,
                            allAppointments = data.upcomingAppointments,
                            specialties = data.specialties
                        )
                    }
                    val currentSelection = _state.value.selectedDate ?: calendarManager.getToday()
                    generateCalendarDates(_state.value.displayedMonth ?: currentSelection)
                    filterAppointments(_state.value.selectedDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date)
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(isLoading = false, error = error.message ?: "Unknown Error")
                    }
                    sendEffect(HomeEffect.ShowError(error.message ?: "Failed to load data"))
                }
            )

            // Fetch avatar in background
            getProfileUseCase().fold(
                onSuccess = { user ->
                    _state.update { it.copy(avatarUrl = user.avatarUrl) }
                },
                onFailure = {}
            )
        }
    }

    private fun filterAppointments(date: LocalDate) {
        val all = _state.value.allAppointments

        val filtered = all.filter { appointment ->
            appointment.date == date
        }

        _state.update { it.copy(filteredAppointments = filtered) }
    }
    private fun sendEffect(effect: HomeEffect) {
        screenModelScope.launch {
            _effect.send(effect)
        }
    }
}
