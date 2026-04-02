package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.domain.model.schedule.*
import org.koin.core.parameter.parametersOf

class ScheduleScreen(private val doctorId: Int) : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<ScheduleViewModel> { parametersOf(doctorId) }
        val selectedTab by viewModel.selectedTab.collectAsState()
        val templatesState by viewModel.templatesState.collectAsState()
        val slotsState by viewModel.slotsState.collectAsState()
        val actionState by viewModel.actionState.collectAsState()
        val showCreateTemplateDialog by viewModel.showCreateTemplateDialog.collectAsState()
        val showCreateSlotDialog by viewModel.showCreateSlotDialog.collectAsState()
        val showApplyTemplateDialog by viewModel.showApplyTemplateDialog.collectAsState()
        val editingTemplate by viewModel.editingTemplate.collectAsState()
        val searchQuery by viewModel.templateSearchQuery.collectAsState()

        // Show snackbar for action results
        val snackbarHostState = remember { SnackbarHostState() }
        LaunchedEffect(actionState) {
            when (val state = actionState) {
                is ActionState.Success -> {
                    snackbarHostState.showSnackbar(state.message)
                    viewModel.clearActionState()
                }
                is ActionState.Error -> {
                    snackbarHostState.showSnackbar(state.message)
                    viewModel.clearActionState()
                }
                else -> {}
            }
        }

        MedAIScaffold(
            topBar = {
                Column {
                    MedAiAppBar(
                        title = "Schedule Management",
                        centerTitle = false
                    )
                    // Tab Row
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = MedAITheme.colors.surface,
                        contentColor = MedAITheme.colors.primary
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { viewModel.onTabSelected(0) },
                            text = {
                                Text(
                                    "Templates",
                                    fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 0) MedAITheme.colors.primary else MedAITheme.colors.text.secondary
                                )
                            },
                            icon = { Icon(Icons.Default.ContentCopy, contentDescription = null) }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { viewModel.onTabSelected(1) },
                            text = {
                                Text(
                                    "Schedule",
                                    fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == 1) MedAITheme.colors.primary else MedAITheme.colors.text.secondary
                                )
                            },
                            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        if (selectedTab == 0) viewModel.showCreateTemplate()
                        else viewModel.showCreateSlot()
                    },
                    containerColor = MedAITheme.colors.primary,
                    contentColor = MedAITheme.colors.onPrimary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (selectedTab) {
                    0 -> TemplatesTab(
                        state = templatesState,
                        searchQuery = searchQuery,
                        onSearchQueryChanged = viewModel::onTemplateSearchQueryChanged,
                        onSearch = viewModel::searchTemplates,
                        onEdit = viewModel::showEditTemplate,
                        onDelete = viewModel::deleteTemplate,
                        onApply = viewModel::showApplyTemplate,
                        onRetry = viewModel::loadTemplates
                    )
                    1 -> SlotsTab(
                        state = slotsState,
                        onDeleteSlot = viewModel::deleteSlot,
                        onRetry = { viewModel.loadSlots() }
                    )
                }

                // Loading overlay for actions
                if (actionState is ActionState.Loading) {
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                }
            }
        }

        // Dialogs
        if (showCreateTemplateDialog) {
            CreateEditTemplateDialog(
                editingTemplate = editingTemplate,
                onDismiss = viewModel::dismissCreateTemplateDialog,
                onCreate = viewModel::createTemplate,
                onUpdate = { name, slots ->
                    editingTemplate?.let { viewModel.updateTemplate(it.id, name, slots) }
                }
            )
        }

        if (showCreateSlotDialog) {
            CreateSlotDialog(
                onDismiss = viewModel::dismissCreateSlotDialog,
                onCreate = viewModel::createSlots
            )
        }

        showApplyTemplateDialog?.let { template ->
            ApplyTemplateDialog(
                templateName = template.name,
                onDismiss = viewModel::dismissApplyTemplateDialog,
                onApply = { start, end -> viewModel.applyTemplate(template.id, start, end) }
            )
        }
    }
}

// ==================== Templates Tab ====================

@Composable
private fun TemplatesTab(
    state: ScheduleUiState<PagedTemplates>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onEdit: (ScheduleTemplate) -> Unit,
    onDelete: (Int) -> Unit,
    onApply: (ScheduleTemplate) -> Unit,
    onRetry: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search templates...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = {
                        onSearchQueryChanged("")
                        onSearch()
                    }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MedAITheme.colors.primary,
                unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f)
            )
        )

        when (state) {
            is ScheduleUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            }
            is ScheduleUiState.Error -> {
                ErrorContent(message = state.message, onRetry = { onRetry(1) })
            }
            is ScheduleUiState.Success -> {
                if (state.data.data.isEmpty()) {
                    EmptyContent(
                        icon = Icons.Default.ContentCopy,
                        message = "No templates found",
                        subtitle = "Create your first schedule template using the + button"
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.data.data) { template ->
                            TemplateCard(
                                template = template,
                                onEdit = { onEdit(template) },
                                onDelete = { onDelete(template.id) },
                                onApply = { onApply(template) }
                            )
                        }
                        // Bottom spacing for FAB
                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun TemplateCard(
    template: ScheduleTemplate,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onApply: () -> Unit
) {
    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = template.name,
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${template.slots.size} slots · Created by ${template.createdByName}",
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Week day pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                weekDays.forEachIndexed { index, dayName ->
                    val isActive = template.slots.any { it.weekDay == index }
                    val bgColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.primary else Color.Transparent
                    )
                    val textColor by animateColorAsState(
                        if (isActive) MedAITheme.colors.onPrimary else MedAITheme.colors.text.tertiary
                    )
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(bgColor)
                            .border(
                                width = 1.dp,
                                color = if (isActive) Color.Transparent else MedAITheme.colors.neutral.copy(alpha = 0.3f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dayName.first().toString(),
                            style = MedAITheme.textStyle.label.small,
                            fontWeight = FontWeight.SemiBold,
                            color = textColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Time slots preview
            val weekDayGroups = template.slots.groupBy { it.weekDay }.entries.sortedBy { it.key }
            val groupedByDayList = weekDayGroups.take(3) // Show first 3 days

            Column {
                groupedByDayList.forEach { entry ->
                    val day = entry.key
                    val slots = entry.value
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = weekDays.getOrElse(day) { "?" },
                            style = MedAITheme.textStyle.label.medium,
                            fontWeight = FontWeight.SemiBold,
                            color = MedAITheme.colors.primary,
                            modifier = Modifier.width(40.dp)
                        )
                        Text(
                            text = slots.joinToString(" · ") { "${it.startTime}-${it.endTime}" },
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            if (groupedByDayList.size < weekDayGroups.size) {
                Text(
                    text = "+${weekDayGroups.size - groupedByDayList.size} more days",
                    style = MedAITheme.textStyle.label.small,
                    color = MedAITheme.colors.text.tertiary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MedAITheme.colors.neutral.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onApply) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Apply", style = MedAITheme.textStyle.label.medium)
                }
                TextButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MedAITheme.textStyle.label.medium)
                }
                TextButton(
                    onClick = { showDeleteConfirm = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MedAITheme.textStyle.label.medium)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Template") },
            text = { Text("Are you sure you want to delete \"${template.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// ==================== Slots Tab ====================

@Composable
private fun SlotsTab(
    state: ScheduleUiState<DoctorSchedule>,
    onDeleteSlot: (Int) -> Unit,
    onRetry: () -> Unit
) {
    when (state) {
        is ScheduleUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MedAITheme.colors.primary)
            }
        }
        is ScheduleUiState.Error -> {
            ErrorContent(message = state.message, onRetry = onRetry)
        }
        is ScheduleUiState.Success -> {
            val schedule = state.data
            if (schedule.days.isEmpty()) {
                EmptyContent(
                    icon = Icons.Default.CalendarMonth,
                    message = "No schedule slots",
                    subtitle = "Create slots manually or apply a template"
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(schedule.days) { day ->
                        DayCard(day = day, onDeleteSlot = onDeleteSlot)
                    }
                    // Bottom spacing for FAB
                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    day: ScheduleDay,
    onDeleteSlot: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = day.day,
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary
                    )
                }
                Text(
                    text = "${day.slots.size} slot${if (day.slots.size != 1) "s" else ""}",
                    style = MedAITheme.textStyle.label.medium,
                    color = MedAITheme.colors.text.secondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Slot list
            day.slots.forEach { slot ->
                SlotRow(slot = slot, onDelete = { onDeleteSlot(slot.id) })
                if (slot != day.slots.last()) {
                    HorizontalDivider(
                        color = MedAITheme.colors.neutral.copy(alpha = 0.1f),
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotRow(
    slot: ScheduleSlot,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val statusColor = when (slot.status) {
        SlotStatus.AVAILABLE -> MedAITheme.colors.status.success
        SlotStatus.BOOKED -> MedAITheme.colors.primary
        SlotStatus.CANCELED -> MedAITheme.colors.status.error
        SlotStatus.COMPLETED -> MedAITheme.colors.text.secondary
    }
    val statusBgColor = when (slot.status) {
        SlotStatus.AVAILABLE -> MedAITheme.colors.status.successContainer
        SlotStatus.BOOKED -> MedAITheme.colors.primary.copy(alpha = 0.1f)
        SlotStatus.CANCELED -> MedAITheme.colors.status.errorContainer
        SlotStatus.COMPLETED -> MedAITheme.colors.neutral.copy(alpha = 0.15f)
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Icon(
                Icons.Default.AccessTime,
                contentDescription = null,
                tint = MedAITheme.colors.text.secondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${slot.startTime.take(5)} - ${slot.endTime.take(5)}",
                style = MedAITheme.textStyle.body.medium,
                fontWeight = FontWeight.Medium,
                color = MedAITheme.colors.text.primary
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Status badge
            Box(
                modifier = Modifier
                    .background(statusBgColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = slot.status.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MedAITheme.textStyle.label.small,
                    fontWeight = FontWeight.SemiBold,
                    color = statusColor
                )
            }

            // Delete button (only for available slots)
            if (slot.status == SlotStatus.AVAILABLE) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Delete slot",
                        tint = MedAITheme.colors.status.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Slot") },
            text = { Text("Delete the ${slot.startTime.take(5)} - ${slot.endTime.take(5)} slot?") },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
            }
        )
    }
}

// ==================== Dialogs ====================

@Composable
private fun CreateEditTemplateDialog(
    editingTemplate: ScheduleTemplate?,
    onDismiss: () -> Unit,
    onCreate: (String, List<ScheduleTemplateSlot>) -> Unit,
    onUpdate: (String?, List<ScheduleTemplateSlot>?) -> Unit
) {
    val isEditing = editingTemplate != null
    var name by remember { mutableStateOf(editingTemplate?.name ?: "") }
    val weekDays = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    // Slot state: map of weekDay -> list of (startTime, endTime)
    val slotsState = remember {
        mutableStateOf(
            editingTemplate?.slots?.groupBy { it.weekDay }?.mapValues { (_, slots) ->
                slots.map { it.startTime to it.endTime }.toMutableList()
            }?.toMutableMap() ?: mutableMapOf()
        )
    }

    var selectedDay by remember { mutableStateOf(0) }
    var newStartTime by remember { mutableStateOf("09:00") }
    var newEndTime by remember { mutableStateOf("10:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Edit Template" else "Create Template") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    placeholder = { Text("e.g. Morning Clinic") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                Text(
                    "Add Time Slots",
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )

                // Day selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    weekDays.forEachIndexed { index, day ->
                        val daySlots = slotsState.value[index]
                        val hasSlotsForDay = daySlots != null && daySlots.isNotEmpty()
                        FilterChip(
                            selected = selectedDay == index,
                            onClick = { selectedDay = index },
                            label = { Text(day.take(3), style = MedAITheme.textStyle.label.small) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MedAITheme.colors.primary,
                                selectedLabelColor = MedAITheme.colors.onPrimary
                            ),
                            border = if (hasSlotsForDay && selectedDay != index)
                                FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = false,
                                    borderColor = MedAITheme.colors.primary.copy(alpha = 0.5f)
                                ) else null,
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }

                // Time inputs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newStartTime,
                        onValueChange = { newStartTime = it },
                        label = { Text("Start") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = newEndTime,
                        onValueChange = { newEndTime = it },
                        label = { Text("End") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    IconButton(
                        onClick = {
                            val currentSlots = slotsState.value.toMutableMap()
                            val daySlots = currentSlots.getOrPut(selectedDay) { mutableListOf() }
                            daySlots.add(newStartTime to newEndTime)
                            slotsState.value = currentSlots
                            newStartTime = "09:00"
                            newEndTime = "10:00"
                        }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add slot", tint = MedAITheme.colors.primary)
                    }
                }

                // Show slots for selected day
                val daySlots = slotsState.value[selectedDay]
                if (daySlots != null && daySlots.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            "${weekDays[selectedDay]} Slots:",
                            style = MedAITheme.textStyle.label.small,
                            color = MedAITheme.colors.text.secondary
                        )
                        daySlots.forEachIndexed { idx, (start, end) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "$start - $end",
                                    style = MedAITheme.textStyle.body.small,
                                    color = MedAITheme.colors.text.primary
                                )
                                IconButton(
                                    onClick = {
                                        val currentSlots = slotsState.value.toMutableMap()
                                        val updatedSlots = currentSlots[selectedDay]?.toMutableList()
                                        updatedSlots?.removeAt(idx)
                                        if (updatedSlots.isNullOrEmpty()) {
                                            currentSlots.remove(selectedDay)
                                        } else {
                                            currentSlots[selectedDay] = updatedSlots
                                        }
                                        slotsState.value = currentSlots
                                    },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                // Summary of all slots
                val totalSlots = slotsState.value.values.sumOf { it.size }
                val activeDays = slotsState.value.keys.size
                if (totalSlots > 0) {
                    Text(
                        "Total: $totalSlots slots across $activeDays day${if (activeDays != 1) "s" else ""}",
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val allSlots = slotsState.value.flatMap { (day, slots) ->
                        slots.map { (start, end) ->
                            ScheduleTemplateSlot(weekDay = day, startTime = start, endTime = end)
                        }
                    }
                    if (isEditing) {
                        onUpdate(name.ifBlank { null }, if (allSlots.isEmpty()) null else allSlots)
                    } else {
                        onCreate(name, allSlots)
                    }
                },
                enabled = name.length >= 5 && slotsState.value.values.any { it.isNotEmpty() },
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                Text(if (isEditing) "Update" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun CreateSlotDialog(
    onDismiss: () -> Unit,
    onCreate: (date: String, startTime: String, endTime: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Schedule Slot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = startTime,
                        onValueChange = { startTime = it },
                        label = { Text("Start Time") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = endTime,
                        onValueChange = { endTime = it },
                        label = { Text("End Time") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(date, startTime, endTime) },
                enabled = date.isNotBlank() && startTime.isNotBlank() && endTime.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun ApplyTemplateDialog(
    templateName: String,
    onDismiss: () -> Unit,
    onApply: (startDate: String, endDate: String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Apply Template") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "Apply \"$templateName\" to generate schedule slots for the selected date range.",
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date") },
                    placeholder = { Text("YYYY-MM-DD") },
                    leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(startDate, endDate) },
                enabled = startDate.isNotBlank() && endDate.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) { Text("Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// ==================== Shared Components ====================

@Composable
private fun EmptyContent(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String,
    subtitle: String
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = MedAITheme.colors.neutral.copy(alpha = 0.4f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MedAITheme.textStyle.title.medium,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MedAITheme.textStyle.body.small,
                color = MedAITheme.colors.text.tertiary
            )
        }
    }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = null,
                tint = MedAITheme.colors.status.error,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.status.error
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                Text("Retry")
            }
        }
    }
}
