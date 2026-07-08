package org.example.project.presentation.schedule

import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import org.example.project.core.presentation.mvi.MviScreenModel
import org.example.project.domain.model.schedule.*
import org.example.project.domain.repository.schedule.CreateScheduleDayInput
import org.example.project.domain.repository.schedule.CreateScheduleSlotInput
import org.example.project.domain.usecase.schedule.*

import org.example.project.core.presentation.util.CalendarManager
import org.example.project.presentation.homeScreen.CalendarUiModel

class ScheduleViewModel(
    private val getScheduleTemplatesUseCase: GetScheduleTemplatesUseCase,
    private val createScheduleTemplateUseCase: CreateScheduleTemplateUseCase,
    private val updateScheduleTemplateUseCase: UpdateScheduleTemplateUseCase,
    private val deleteScheduleTemplateUseCase: DeleteScheduleTemplateUseCase,
    private val applyScheduleTemplateUseCase: ApplyScheduleTemplateUseCase,
    private val getScheduleSlotsUseCase: GetScheduleSlotsUseCase,
    private val createScheduleSlotsUseCase: CreateScheduleSlotsUseCase,
    private val updateScheduleSlotUseCase: UpdateScheduleSlotUseCase,
    private val deleteScheduleSlotUseCase: DeleteScheduleSlotUseCase,
    private val calendarManager: CalendarManager,
    private val doctorId: Int
) : MviScreenModel<ScheduleState, ScheduleEvent, ScheduleEffect>(ScheduleState()) {

    init {
        val today = calendarManager.getToday()
        setState {
            copy(
                selectedDate = today,
                displayedMonth = today
            )
        }
        generateCalendar(today)
        loadTemplates()
        loadSlots()
    }

    override fun onEvent(event: ScheduleEvent) {
        when (event) {
            is ScheduleEvent.RefreshCurrentTab -> {
                if (state.value.selectedTab == 0) loadTemplates() else loadSlots()
            }
            is ScheduleEvent.OnTabSelected -> {
                setState { copy(selectedTab = event.tab) }
                if (event.tab == 0) loadTemplates() else loadSlots()
            }
            is ScheduleEvent.LoadTemplates -> loadTemplates(event.page)
            is ScheduleEvent.OnTemplateSearchQueryChanged -> {
                setState { copy(templateSearchQuery = event.query) }
            }
            is ScheduleEvent.SearchTemplates -> loadTemplates()
            is ScheduleEvent.ShowCreateTemplate -> {
                setState { copy(editingTemplate = null, showCreateTemplateDialog = true) }
            }
            is ScheduleEvent.ShowEditTemplate -> {
                setState { copy(editingTemplate = event.template, showCreateTemplateDialog = true) }
            }
            is ScheduleEvent.DismissCreateTemplateDialog -> {
                setState { copy(showCreateTemplateDialog = false, editingTemplate = null) }
            }
            is ScheduleEvent.CreateTemplate -> createTemplate(event.name, event.slots)
            is ScheduleEvent.UpdateTemplate -> updateTemplate(event.templateId, event.name, event.slots)
            is ScheduleEvent.DeleteTemplate -> deleteTemplate(event.templateId)
            is ScheduleEvent.ShowApplyTemplate -> {
                setState { copy(showApplyTemplateDialog = event.template) }
            }
            is ScheduleEvent.DismissApplyTemplateDialog -> {
                setState { copy(showApplyTemplateDialog = null) }
            }
            is ScheduleEvent.ApplyTemplate -> applyTemplate(event.templateId, event.startDate, event.endDate)
            is ScheduleEvent.LoadSlots -> loadSlots(event.page, event.fromDate, event.toDate)
            is ScheduleEvent.LoadNextSlotsPage -> loadNextSlotsPage()
            is ScheduleEvent.ShowCreateSlot -> {
                setState { copy(showCreateSlotDialog = true) }
            }
            is ScheduleEvent.DismissCreateSlotDialog -> {
                setState { copy(showCreateSlotDialog = false) }
            }
            is ScheduleEvent.CreateSlots -> createSlots(event.date, event.startTime, event.endTime)
            is ScheduleEvent.DeleteSlot -> deleteSlot(event.slotId)
            is ScheduleEvent.DateSelected -> {
                if (state.value.selectedDate != event.date) {
                    setState { copy(selectedDate = event.date) }
                    state.value.displayedMonth?.let { generateCalendar(it) }
                }
            }
            is ScheduleEvent.PrevMonthClicked -> {
                val current = state.value.displayedMonth ?: return
                val newMonth = calendarManager.getPreviousMonth(current)
                setState { copy(displayedMonth = newMonth) }
                loadSlotsForMonth(newMonth)
            }
            is ScheduleEvent.NextMonthClicked -> {
                val current = state.value.displayedMonth ?: return
                val newMonth = calendarManager.getNextMonth(current)
                setState { copy(displayedMonth = newMonth) }
                loadSlotsForMonth(newMonth)
            }
        }
    }

    private fun loadTemplates(page: Int = 1) {
        screenModelScope.launch {
            setState { copy(templatesState = ScheduleUiState.Loading) }
            getScheduleTemplatesUseCase(
                pageNo = page,
                pageSize = 10,
                name = state.value.templateSearchQuery.ifBlank { null },
                doctorId = doctorId
            ).onSuccess {
                setState { copy(templatesState = ScheduleUiState.Success(it)) }
            }.onFailure {
                setState { copy(templatesState = ScheduleUiState.Error(it.message ?: "Failed to load templates")) }
            }
        }
    }

    private fun createTemplate(name: String, slots: List<ScheduleTemplateSlot>) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            createScheduleTemplateUseCase(doctorId, name, slots)
                .onSuccess {
                    setState { copy(isActionLoading = false, showCreateTemplateDialog = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar("Template created successfully"))
                    loadTemplates()
                }
                .onFailure {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to create template", isError = true))
                }
        }
    }

    private fun updateTemplate(templateId: Int, name: String?, slots: List<ScheduleTemplateSlot>?) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            updateScheduleTemplateUseCase(doctorId, templateId, name, slots)
                .onSuccess {
                    setState { copy(isActionLoading = false, showCreateTemplateDialog = false, editingTemplate = null) }
                    sendEffect(ScheduleEffect.ShowSnackbar("Template updated successfully"))
                    loadTemplates()
                }
                .onFailure {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to update template", isError = true))
                }
        }
    }

    private fun deleteTemplate(templateId: Int) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            deleteScheduleTemplateUseCase(doctorId, templateId)
                .onSuccess {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar("Template deleted"))
                    loadTemplates()
                }
                .onFailure {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to delete template", isError = true))
                }
        }
    }

    private fun applyTemplate(templateId: Int, startDate: String, endDate: String) {
        val start = try { LocalDate.parse(startDate) } catch (e: Exception) { null }
        val end = try { LocalDate.parse(endDate) } catch (e: Exception) { null }
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        if (start == null || end == null) {
            sendEffect(ScheduleEffect.ShowSnackbar("Invalid date format", isError = true))
            return
        }
        if (start < today || end < today) {
            sendEffect(ScheduleEffect.ShowSnackbar("Cannot apply template to past dates", isError = true))
            return
        }
        if (start > end) {
            sendEffect(ScheduleEffect.ShowSnackbar("Start date cannot be after end date", isError = true))
            return
        }

        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            applyScheduleTemplateUseCase(doctorId, templateId, startDate, endDate)
                .onSuccess {
                    setState {
                        copy(
                            isActionLoading = false,
                            showApplyTemplateDialog = null,
                            selectedTab = 1,
                            selectedDate = start,
                            displayedMonth = start
                        )
                    }
                    sendEffect(ScheduleEffect.ShowSnackbar("Template applied successfully"))
                    loadSlotsForMonth(start!!)
                }
                .onFailure {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to apply template", isError = true))
                }
        }
    }

    private fun loadSlotsForMonth(month: LocalDate) {
        val firstDay = LocalDate(month.year, month.monthNumber, 1)
        val lastDay = firstDay.plus(DatePeriod(months = 1)).plus(DatePeriod(days = -1))
        loadSlots(fromDate = firstDay.toString(), toDate = lastDay.toString())
    }

    private fun loadSlots(page: Int = 1, fromDate: String? = null, toDate: String? = null) {
        val effectiveFromDate = fromDate ?: state.value.displayedMonth?.let {
            LocalDate(it.year, it.monthNumber, 1).toString()
        }
        val effectiveToDate = toDate ?: state.value.displayedMonth?.let {
            val firstDay = LocalDate(it.year, it.monthNumber, 1)
            firstDay.plus(DatePeriod(months = 1)).plus(DatePeriod(days = -1)).toString()
        }

        screenModelScope.launch {
            if (page == 1) {
                setState { copy(slotsState = ScheduleUiState.Loading) }
            } else {
                setState { copy(isSlotsPaginating = true) }
            }
            getScheduleSlotsUseCase(
                doctorId = doctorId,
                fromDate = effectiveFromDate,
                toDate = effectiveToDate,
                pageNo = page,
                pageSize = 31
            ).onSuccess {
                setState {
                    copy(
                        slotsState = ScheduleUiState.Success(it),
                        isSlotsPaginating = false
                    )
                }
                state.value.displayedMonth?.let { generateCalendar(it) }
            }.onFailure {
                setState {
                    copy(
                        slotsState = if (page == 1) ScheduleUiState.Error(it.message ?: "Failed to load schedule slots") else slotsState,
                        isSlotsPaginating = false
                    )
                }
                if (page > 1) {
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to load more slots", isError = true))
                }
            }
        }
    }

    private fun loadNextSlotsPage() {
        val currentState = state.value.slotsState
        if (currentState is ScheduleUiState.Success) {
            val currentSchedule = currentState.data
            if (currentSchedule.hasNext && !state.value.isSlotsPaginating) {
                setState { copy(isSlotsPaginating = true) }
                screenModelScope.launch {
                    getScheduleSlotsUseCase(
                        doctorId = doctorId,
                        pageNo = currentSchedule.currentPage + 1,
                        pageSize = 10
                    ).onSuccess { nextSchedule ->
                        setState {
                            val combinedDays = currentSchedule.days + nextSchedule.days
                            copy(
                                isSlotsPaginating = false,
                                slotsState = ScheduleUiState.Success(
                                    currentSchedule.copy(
                                        days = combinedDays,
                                        currentPage = nextSchedule.currentPage,
                                        hasNext = nextSchedule.hasNext,
                                        hasPrevious = nextSchedule.hasPrevious
                                    )
                                )
                            )
                        }
                    }.onFailure {
                        setState { copy(isSlotsPaginating = false) }
                        sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to load more slots", isError = true))
                    }
                }
            }
        }
    }

    private fun createSlots(date: String, startTime: String, endTime: String) {
        val selectedDate = try { LocalDate.parse(date) } catch (e: Exception) { null }
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date

        if (selectedDate == null) {
            sendEffect(ScheduleEffect.ShowSnackbar("Invalid date format", isError = true))
            return
        }
        if (selectedDate < today) {
            sendEffect(ScheduleEffect.ShowSnackbar("Cannot create slots in the past", isError = true))
            return
        }

        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            createScheduleSlotsUseCase(
                doctorId,
                listOf(
                    CreateScheduleDayInput(
                        date = date,
                        slots = listOf(CreateScheduleSlotInput(startTime, endTime))
                    )
                )
            ).onSuccess {
                setState { copy(isActionLoading = false, showCreateSlotDialog = false) }
                sendEffect(ScheduleEffect.ShowSnackbar("Slot created successfully"))
                loadSlots()
            }.onFailure {
                setState { copy(isActionLoading = false) }
                sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to create slot", isError = true))
            }
        }
    }

    private fun deleteSlot(slotId: Int) {
        screenModelScope.launch {
            setState { copy(isActionLoading = true) }
            deleteScheduleSlotUseCase(doctorId, slotId)
                .onSuccess {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar("Slot deleted"))
                    loadSlots()
                }
                .onFailure {
                    setState { copy(isActionLoading = false) }
                    sendEffect(ScheduleEffect.ShowSnackbar(it.message ?: "Failed to delete slot", isError = true))
                }
        }
    }

    private fun generateCalendar(baseDate: LocalDate) {
        val days = calendarManager.getDaysForMonth(baseDate)
        val selected = state.value.selectedDate ?: calendarManager.getToday()
        val successSlots = (state.value.slotsState as? ScheduleUiState.Success)?.data?.days ?: emptyList()

        val uiDays = days.map { date ->
            val hasSlots = successSlots.any { it.day == date.toString() && it.slots.isNotEmpty() }
            CalendarUiModel(
                day = date.dayOfMonth.toString(),
                weekDay = date.dayOfWeek.name.take(3),
                fullDate = date,
                isSelected = date == selected,
                hasAppointment = hasSlots
            )
        }
        setState { copy(calendarDays = uiDays) }
    }
}
