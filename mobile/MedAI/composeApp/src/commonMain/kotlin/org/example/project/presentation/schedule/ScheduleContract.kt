package org.example.project.presentation.schedule

import org.example.project.domain.model.schedule.DoctorSchedule
import org.example.project.domain.model.schedule.PagedTemplates
import org.example.project.domain.model.schedule.ScheduleTemplate
import org.example.project.domain.model.schedule.ScheduleTemplateSlot

sealed class ScheduleUiState<out T> {
    data object Loading : ScheduleUiState<Nothing>()
    data class Success<T>(val data: T) : ScheduleUiState<T>()
    data class Error(val message: String) : ScheduleUiState<Nothing>()
}

data class ScheduleState(
    val selectedTab: Int = 0, // 0 = Templates, 1 = Slots

    // Templates State
    val templatesState: ScheduleUiState<PagedTemplates> = ScheduleUiState.Loading,
    val templateSearchQuery: String = "",

    // Slots State
    val slotsState: ScheduleUiState<DoctorSchedule> = ScheduleUiState.Loading,

    // Action State (Loading Overlay)
    val isActionLoading: Boolean = false,

    // Dialog State
    val showCreateTemplateDialog: Boolean = false,
    val showCreateSlotDialog: Boolean = false,
    val showApplyTemplateDialog: ScheduleTemplate? = null,
    val editingTemplate: ScheduleTemplate? = null
)

sealed interface ScheduleEvent {
    data object RefreshCurrentTab : ScheduleEvent
    data class OnTabSelected(val tab: Int) : ScheduleEvent

    // Templates
    data class LoadTemplates(val page: Int = 1) : ScheduleEvent
    data class OnTemplateSearchQueryChanged(val query: String) : ScheduleEvent
    data object SearchTemplates : ScheduleEvent
    data object ShowCreateTemplate : ScheduleEvent
    data class ShowEditTemplate(val template: ScheduleTemplate) : ScheduleEvent
    data object DismissCreateTemplateDialog : ScheduleEvent
    data class CreateTemplate(val name: String, val slots: List<ScheduleTemplateSlot>) : ScheduleEvent
    data class UpdateTemplate(val templateId: Int, val name: String?, val slots: List<ScheduleTemplateSlot>?) : ScheduleEvent
    data class DeleteTemplate(val templateId: Int) : ScheduleEvent
    data class ShowApplyTemplate(val template: ScheduleTemplate) : ScheduleEvent
    data object DismissApplyTemplateDialog : ScheduleEvent
    data class ApplyTemplate(val templateId: Int, val startDate: String, val endDate: String) : ScheduleEvent

    // Slots
    data class LoadSlots(val page: Int = 1, val fromDate: String? = null, val toDate: String? = null) : ScheduleEvent
    data object ShowCreateSlot : ScheduleEvent
    data object DismissCreateSlotDialog : ScheduleEvent
    data class CreateSlots(val date: String, val startTime: String, val endTime: String) : ScheduleEvent
    data class DeleteSlot(val slotId: Int) : ScheduleEvent
}

sealed interface ScheduleEffect {
    data class ShowSnackbar(val message: String, val isError: Boolean = false) : ScheduleEffect
}
