package org.example.project.presentation.schedule

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.verticalScroll
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
                slots.map { normalizeTimeStr(it.startTime) to normalizeTimeStr(it.endTime) }.toMutableList()
            }?.toMutableMap() ?: mutableMapOf()
        )
    }

    var selectedDay by remember { mutableStateOf(0) }
    var newStartTime by remember { mutableStateOf("09:00") }
    var newEndTime by remember { mutableStateOf("10:00") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (isEditing) "Edit Template" else "Create Template",
                style = MedAITheme.textStyle.headline.small,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.primary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Template Name") },
                    placeholder = { Text("e.g. Morning Shifts") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MedAITheme.colors.primary,
                        unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.2f)
                    )
                )

                Text(
                    "Select Weekly Days",
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )

                // Day selector chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    weekDays.forEachIndexed { index, day ->
                        val daySlots = slotsState.value[index]
                        val hasSlotsForDay = !daySlots.isNullOrEmpty()

                        val isSelected = selectedDay == index
                        val bgColor by animateColorAsState(
                            if (isSelected) MedAITheme.colors.primary
                            else if (hasSlotsForDay) MedAITheme.colors.primary.copy(alpha = 0.08f)
                            else Color.Transparent
                        )
                        val textColor by animateColorAsState(
                            if (isSelected) MedAITheme.colors.onPrimary
                            else if (hasSlotsForDay) MedAITheme.colors.primary
                            else MedAITheme.colors.text.tertiary
                        )
                        val borderColor by animateColorAsState(
                            if (isSelected) Color.Transparent
                            else if (hasSlotsForDay) MedAITheme.colors.primary.copy(alpha = 0.3f)
                            else MedAITheme.colors.neutral.copy(alpha = 0.2f)
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .clickable {
                                    selectedDay = index
                                    errorMessage = null
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .border(1.dp, borderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.take(3),
                                    style = MedAITheme.textStyle.label.small,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                            }
                            // Dot indicator
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (hasSlotsForDay) MedAITheme.colors.primary else Color.Transparent)
                            )
                        }
                    }
                }

                Text(
                    "Define Hours for ${weekDays[selectedDay]}",
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )

                // Time range fields + Add button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newStartTime,
                        onValueChange = {
                            newStartTime = it
                            errorMessage = null
                        },
                        label = { Text("Start") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedAITheme.colors.primary,
                            unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.2f)
                        )
                    )
                    OutlinedTextField(
                        value = newEndTime,
                        onValueChange = {
                            newEndTime = it
                            errorMessage = null
                        },
                        label = { Text("End") },
                        placeholder = { Text("HH:MM") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MedAITheme.colors.primary,
                            unfocusedBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.2f)
                        )
                    )
                    IconButton(
                        onClick = {
                            val currentSlots = slotsState.value.toMutableMap()
                            val daySlots = currentSlots[selectedDay]?.toMutableList() ?: mutableListOf()
                            val normStart = normalizeTimeStr(newStartTime)
                            val normEnd = normalizeTimeStr(newEndTime)
                            val hasOverlap = daySlots.any { (start, end) ->
                                start < normEnd && normStart < end
                            }
                            if (hasOverlap) {
                                errorMessage = "This slot overlaps with an existing slot on ${weekDays[selectedDay]}."
                            } else {
                                errorMessage = null
                                daySlots.add(normStart to normEnd)
                                currentSlots[selectedDay] = daySlots
                                slotsState.value = currentSlots
                                newStartTime = "09:00"
                                newEndTime = "10:00"
                            }
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(MedAITheme.colors.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add slot", tint = MedAITheme.colors.primary)
                    }
                }

                // Validation Error Message
                errorMessage?.let { msg ->
                    Text(
                        text = msg,
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.status.error,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Show slots for selected day
                val daySlots = slotsState.value[selectedDay]
                if (!daySlots.isNullOrEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "Added Slots:",
                            style = MedAITheme.textStyle.label.small,
                            color = MedAITheme.colors.text.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        daySlots.forEachIndexed { idx, (start, end) ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.neutral.copy(alpha = 0.03f)),
                                border = BorderStroke(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = MedAITheme.colors.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "$start - $end",
                                            style = MedAITheme.textStyle.body.medium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MedAITheme.colors.text.primary
                                        )
                                    }
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
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.DeleteOutline,
                                            contentDescription = "Remove",
                                            tint = MedAITheme.colors.status.error,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MedAITheme.colors.neutral.copy(alpha = 0.02f), RoundedCornerShape(12.dp))
                            .border(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No slots added for this day yet.",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.tertiary
                        )
                    }
                }

                // Summary of all slots
                val totalSlots = slotsState.value.values.sumOf { it.size }
                val activeDays = slotsState.value.keys.size
                if (totalSlots > 0) {
                    Text(
                        "Summary: $totalSlots slots configured across $activeDays working day${if (activeDays != 1) "s" else ""}",
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.primary,
                        fontWeight = FontWeight.Bold
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) {
                Text(if (isEditing) "Save Changes" else "Create Template", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MedAITheme.colors.text.secondary)
            }
        }
    )
}

@Composable
fun CreateSlotDialog(
    initialDate: String = "",
    onDismiss: () -> Unit,
    onCreate: (date: String, startTime: String, endTime: String) -> Unit
) {
    var date by remember { mutableStateOf(initialDate) }
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
                onClick = { onCreate(date, normalizeTimeStr(startTime), normalizeTimeStr(endTime)) },
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
            allowPastDates = false,
            outputFormat = "YYYY-MM-DD"
        )
    }
}

@Composable
fun ApplyTemplateDialog(
    template: ScheduleTemplate,
    onDismiss: () -> Unit,
    onApply: (startDate: String, endDate: String) -> Unit
) {
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    val weekDays = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                "Apply Template",
                style = MedAITheme.textStyle.headline.small,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.primary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    "Apply this recurring rule to generate working hours for a future date range in bulk.",
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.text.secondary
                )

                // 1. Template Overview Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.neutral.copy(alpha = 0.03f)),
                    border = BorderStroke(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = template.name,
                            style = MedAITheme.textStyle.title.medium,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${template.slots.size} weekly slots scheduled",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        // Active days display
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            weekDays.forEachIndexed { index, dayName ->
                                val isActive = template.slots.any { it.weekDay == index }
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isActive) MedAITheme.colors.primary.copy(alpha = 0.15f)
                                            else MedAITheme.colors.neutral.copy(alpha = 0.05f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = dayName.take(1),
                                        style = MedAITheme.textStyle.label.small,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isActive) MedAITheme.colors.primary else MedAITheme.colors.text.tertiary
                                    )
                                }
                            }
                        }
                    }
                }

                // 2. Start Date box
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
                            disabledBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.2f),
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

                // 3. End Date box
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
                            disabledBorderColor = MedAITheme.colors.neutral.copy(alpha = 0.2f),
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
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MedAITheme.colors.primary)
            ) { Text("Apply Template", fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = MedAITheme.colors.text.secondary) }
        }
    )

    if (showStartDatePicker) {
        MedAIDatePickerDialog(
            onDateSelected = { startDate = it },
            onDismiss = { showStartDatePicker = false },
            allowFutureDates = true,
            allowPastDates = false,
            outputFormat = "YYYY-MM-DD"
        )
    }

    if (showEndDatePicker) {
        MedAIDatePickerDialog(
            onDateSelected = { endDate = it },
            onDismiss = { showEndDatePicker = false },
            allowFutureDates = true,
            allowPastDates = false,
            outputFormat = "YYYY-MM-DD"
        )
    }
}

private fun normalizeTimeStr(timeStr: String): String {
    val trimmed = timeStr.trim()
    val parts = trimmed.split(":")
    if (parts.size < 2) return trimmed
    val hour = parts[0].padStart(2, '0')
    val minute = parts[1].padStart(2, '0')
    return "$hour:$minute"
}
