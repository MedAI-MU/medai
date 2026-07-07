package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import org.example.project.design_system.component.dayPicker.MedAIDatePickerDialog
import org.example.project.domain.model.schedule.*
import org.koin.core.parameter.parametersOf


// ==================== Dialogs ====================

@Composable
fun CreateEditTemplateDialog(
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
fun CreateSlotDialog(
    onDismiss: () -> Unit,
    onCreate: (date: String, startTime: String, endTime: String) -> Unit
) {
    var date by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("09:00") }
    var endTime by remember { mutableStateOf("10:00") }
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Schedule Slot") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MedAITheme.colors.text.primary,
                            disabledBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f),
                            disabledLabelColor = MedAITheme.colors.text.secondary,
                            disabledLeadingIconColor = MedAITheme.colors.text.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }
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

    if (showDatePicker) {
        MedAIDatePickerDialog(
            onDateSelected = { date = it },
            onDismiss = { showDatePicker = false },
            allowFutureDates = true,
            outputFormat = "YYYY-MM-DD"
        )
    }
}

@Composable
fun ApplyTemplateDialog(
    templateName: String,
    onDismiss: () -> Unit,
    onApply: (startDate: String, endDate: String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

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
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = startDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("Start Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MedAITheme.colors.text.primary,
                            disabledBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f),
                            disabledLabelColor = MedAITheme.colors.text.secondary,
                            disabledLeadingIconColor = MedAITheme.colors.text.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showStartDatePicker = true }
                    )
                }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = endDate,
                        onValueChange = {},
                        readOnly = true,
                        enabled = false,
                        label = { Text("End Date") },
                        placeholder = { Text("YYYY-MM-DD") },
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MedAITheme.colors.text.primary,
                            disabledBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.3f),
                            disabledLabelColor = MedAITheme.colors.text.secondary,
                            disabledLeadingIconColor = MedAITheme.colors.text.secondary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showEndDatePicker = true }
                    )
                }
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

    if (showStartDatePicker) {
        MedAIDatePickerDialog(
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false },
            allowFutureDates = true,
            outputFormat = "YYYY-MM-DD"
        )
    }

    if (showEndDatePicker) {
        MedAIDatePickerDialog(
            onDateSelected = { endDate = it },
            onDismiss = { showEndDatePicker = false },
            allowFutureDates = true,
            outputFormat = "YYYY-MM-DD"
        )
    }
}
