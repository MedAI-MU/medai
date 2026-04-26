package org.example.project.presentation.schedule

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.schedule.*
import org.example.project.domain.repository.CreateScheduleDayInput
import org.example.project.domain.repository.CreateScheduleSlotInput
import org.example.project.domain.usecase.schedule.*

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
    private val doctorId: Int
) : ScreenModel {

    // --- Tab State ---
    private val _selectedTab = MutableStateFlow(0) // 0 = Templates, 1 = Slots
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // --- Templates State ---
    private val _templatesState = MutableStateFlow<ScheduleUiState<PagedTemplates>>(ScheduleUiState.Loading)
    val templatesState: StateFlow<ScheduleUiState<PagedTemplates>> = _templatesState.asStateFlow()

    private val _templateSearchQuery = MutableStateFlow("")
    val templateSearchQuery: StateFlow<String> = _templateSearchQuery.asStateFlow()

    // --- Slots State ---
    private val _slotsState = MutableStateFlow<ScheduleUiState<DoctorSchedule>>(ScheduleUiState.Loading)
    val slotsState: StateFlow<ScheduleUiState<DoctorSchedule>> = _slotsState.asStateFlow()

    // --- Action State ---
    private val _actionState = MutableStateFlow<ActionState>(ActionState.Idle)
    val actionState: StateFlow<ActionState> = _actionState.asStateFlow()

    // --- Dialog State ---
    private val _showCreateTemplateDialog = MutableStateFlow(false)
    val showCreateTemplateDialog: StateFlow<Boolean> = _showCreateTemplateDialog.asStateFlow()

    private val _showCreateSlotDialog = MutableStateFlow(false)
    val showCreateSlotDialog: StateFlow<Boolean> = _showCreateSlotDialog.asStateFlow()

    private val _showApplyTemplateDialog = MutableStateFlow<ScheduleTemplate?>(null)
    val showApplyTemplateDialog: StateFlow<ScheduleTemplate?> = _showApplyTemplateDialog.asStateFlow()

    private val _editingTemplate = MutableStateFlow<ScheduleTemplate?>(null)
    val editingTemplate: StateFlow<ScheduleTemplate?> = _editingTemplate.asStateFlow()

    init {
        loadTemplates()
        loadSlots()
    }

    fun onTabSelected(tab: Int) {
        _selectedTab.value = tab
    }

    // --- Templates ---

    fun loadTemplates(page: Int = 1) {
        screenModelScope.launch {
            _templatesState.value = ScheduleUiState.Loading
            getScheduleTemplatesUseCase(
                pageNo = page,
                pageSize = 10,
                name = _templateSearchQuery.value.ifBlank { null },
                doctorId = doctorId
            ).onSuccess {
                _templatesState.value = ScheduleUiState.Success(it)
            }.onFailure {
                _templatesState.value = ScheduleUiState.Error(it.message ?: "Failed to load templates")
            }
        }
    }

    fun onTemplateSearchQueryChanged(query: String) {
        _templateSearchQuery.value = query
    }

    fun searchTemplates() {
        loadTemplates()
    }

    fun showCreateTemplate() {
        _editingTemplate.value = null
        _showCreateTemplateDialog.value = true
    }

    fun showEditTemplate(template: ScheduleTemplate) {
        _editingTemplate.value = template
        _showCreateTemplateDialog.value = true
    }

    fun dismissCreateTemplateDialog() {
        _showCreateTemplateDialog.value = false
        _editingTemplate.value = null
    }

    fun createTemplate(name: String, slots: List<ScheduleTemplateSlot>) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            createScheduleTemplateUseCase(doctorId, name, slots)
                .onSuccess {
                    _actionState.value = ActionState.Success("Template created successfully")
                    _showCreateTemplateDialog.value = false
                    loadTemplates()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to create template")
                }
        }
    }

    fun updateTemplate(templateId: Int, name: String?, slots: List<ScheduleTemplateSlot>?) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            updateScheduleTemplateUseCase(doctorId, templateId, name, slots)
                .onSuccess {
                    _actionState.value = ActionState.Success("Template updated successfully")
                    _showCreateTemplateDialog.value = false
                    _editingTemplate.value = null
                    loadTemplates()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to update template")
                }
        }
    }

    fun deleteTemplate(templateId: Int) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            deleteScheduleTemplateUseCase(doctorId, templateId)
                .onSuccess {
                    _actionState.value = ActionState.Success("Template deleted")
                    loadTemplates()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to delete template")
                }
        }
    }

    fun showApplyTemplate(template: ScheduleTemplate) {
        _showApplyTemplateDialog.value = template
    }

    fun dismissApplyTemplateDialog() {
        _showApplyTemplateDialog.value = null
    }

    fun applyTemplate(templateId: Int, startDate: String, endDate: String) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            applyScheduleTemplateUseCase(doctorId, templateId, startDate, endDate)
                .onSuccess {
                    _actionState.value = ActionState.Success("Template applied successfully")
                    _showApplyTemplateDialog.value = null
                    loadSlots()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to apply template")
                }
        }
    }

    // --- Slots ---

    fun loadSlots(page: Int = 1, fromDate: String? = null, toDate: String? = null) {
        screenModelScope.launch {
            _slotsState.value = ScheduleUiState.Loading
            getScheduleSlotsUseCase(
                doctorId = doctorId,
                fromDate = fromDate,
                toDate = toDate,
                pageNo = page,
                pageSize = 10
            ).onSuccess {
                _slotsState.value = ScheduleUiState.Success(it)
            }.onFailure {
                _slotsState.value = ScheduleUiState.Error(it.message ?: "Failed to load schedule slots")
            }
        }
    }

    fun showCreateSlot() {
        _showCreateSlotDialog.value = true
    }

    fun dismissCreateSlotDialog() {
        _showCreateSlotDialog.value = false
    }

    fun createSlots(date: String, startTime: String, endTime: String) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            createScheduleSlotsUseCase(
                doctorId,
                listOf(
                    CreateScheduleDayInput(
                        date = date,
                        slots = listOf(CreateScheduleSlotInput(startTime, endTime))
                    )
                )
            ).onSuccess {
                _actionState.value = ActionState.Success("Slot created successfully")
                _showCreateSlotDialog.value = false
                loadSlots()
            }.onFailure {
                _actionState.value = ActionState.Error(it.message ?: "Failed to create slot")
            }
        }
    }

    fun deleteSlot(slotId: Int) {
        screenModelScope.launch {
            _actionState.value = ActionState.Loading
            deleteScheduleSlotUseCase(doctorId, slotId)
                .onSuccess {
                    _actionState.value = ActionState.Success("Slot deleted")
                    loadSlots()
                }
                .onFailure {
                    _actionState.value = ActionState.Error(it.message ?: "Failed to delete slot")
                }
        }
    }

    fun clearActionState() {
        _actionState.value = ActionState.Idle
    }
}

sealed class ScheduleUiState<out T> {
    data object Loading : ScheduleUiState<Nothing>()
    data class Success<T>(val data: T) : ScheduleUiState<T>()
    data class Error(val message: String) : ScheduleUiState<Nothing>()
}

sealed class ActionState {
    data object Idle : ActionState()
    data object Loading : ActionState()
    data class Success(val message: String) : ActionState()
    data class Error(val message: String) : ActionState()
}
