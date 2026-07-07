package org.example.project.presentation.schedule

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.datetime.LocalDate
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import org.example.project.domain.model.schedule.*
import org.example.project.presentation.homeScreen.CalendarUiModel

@Composable
fun SlotsTab(
    scheduleState: ScheduleState,
    onDateSelected: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onDeleteSlot: (Int) -> Unit,
    onRetry: () -> Unit,
    onApplyTemplate: () -> Unit,
    onCreateSlot: () -> Unit
) {
    Crossfade(targetState = scheduleState.slotsState, label = "SlotsContent") { state ->
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
                val selectedDateStr = scheduleState.selectedDate?.toString() ?: ""
                val selectedDay = schedule.days.find { it.day == selectedDateStr }
                val selectedDaySlots = selectedDay?.slots ?: emptyList()

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Horizontal Calendar Selection Strip
                    scheduleState.displayedMonth?.let { month ->
                        DaySelectorStrip(
                            displayedMonth = month,
                            calendarDays = scheduleState.calendarDays,
                            onDateSelected = onDateSelected,
                            onPrevMonth = onPrevMonth,
                            onNextMonth = onNextMonth
                        )
                    }

                    if (selectedDaySlots.isNotEmpty()) {
                        // Slot Metrics banner
                        SlotStatOverview(slots = selectedDaySlots)

                        // Time Slots Grid List
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            items(selectedDaySlots) { slot ->
                                TimeSlotCard(
                                    slot = slot,
                                    onDelete = { onDeleteSlot(slot.id) }
                                )
                            }

                            // FAB spacer bottom item
                            item(span = { GridItemSpan(2) }) {
                                Spacer(modifier = Modifier.height(80.dp))
                            }
                        }
                    } else {
                        // Dynamic Empty placeholder action state
                        EmptySlotContent(
                            onApplyTemplate = onApplyTemplate,
                            onCreateSlot = onCreateSlot,
                            modifier = Modifier.weight(1f).wrapContentHeight(Alignment.CenterVertically)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DaySelectorStrip(
    displayedMonth: LocalDate,
    calendarDays: List<CalendarUiModel>,
    onDateSelected: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val monthLabel = "${displayedMonth.month.name.lowercase().replaceFirstChar { it.uppercase() }} ${displayedMonth.year}"
            Text(
                text = monthLabel,
                style = MedAITheme.textStyle.title.medium,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(onClick = onPrevMonth, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.ChevronLeft,
                        contentDescription = "Previous Month",
                        tint = MedAITheme.colors.text.primary
                    )
                }
                IconButton(onClick = onNextMonth, modifier = Modifier.size(36.dp)) {
                    Icon(
                        Icons.Default.ChevronRight,
                        contentDescription = "Next Month",
                        tint = MedAITheme.colors.text.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(horizontal = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(calendarDays) { day ->
                MedAIDateCard(
                    day = day.day,
                    weekday = day.weekDay,
                    isSelected = day.isSelected,
                    hasAppointment = day.hasAppointment,
                    onClick = { onDateSelected(day.fullDate) }
                )
            }
        }
    }
}

@Composable
fun SlotStatOverview(
    slots: List<ScheduleSlot>,
    modifier: Modifier = Modifier
) {
    val total = slots.size
    val available = slots.count { it.status == SlotStatus.AVAILABLE }
    val booked = slots.count { it.status == SlotStatus.BOOKED }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MedAITheme.colors.surface, RoundedCornerShape(16.dp))
            .border(1.dp, MedAITheme.colors.neutral.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
            .padding(vertical = 14.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StatItem(
            label = "Total Slots",
            value = total.toString(),
            color = MedAITheme.colors.text.primary,
            bgColor = MedAITheme.colors.neutral.copy(alpha = 0.08f)
        )
        Spacer(modifier = Modifier.width(1.dp).height(24.dp).background(MedAITheme.colors.neutral.copy(alpha = 0.1f)))
        StatItem(
            label = "Available",
            value = available.toString(),
            color = MedAITheme.colors.status.success,
            bgColor = MedAITheme.colors.status.successContainer
        )
        Spacer(modifier = Modifier.width(1.dp).height(24.dp).background(MedAITheme.colors.neutral.copy(alpha = 0.1f)))
        StatItem(
            label = "Booked",
            value = booked.toString(),
            color = MedAITheme.colors.primary,
            bgColor = MedAITheme.colors.primary.copy(alpha = 0.1f)
        )
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    color: Color,
    bgColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(bgColor, CircleShape)
                .padding(horizontal = 10.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = value,
                style = MedAITheme.textStyle.label.medium,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
        Text(
            text = label,
            style = MedAITheme.textStyle.label.medium,
            color = MedAITheme.colors.text.secondary,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TimeSlotCard(
    slot: ScheduleSlot,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
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

    val statusLabel = when (slot.status) {
        SlotStatus.AVAILABLE -> "Available"
        SlotStatus.BOOKED -> "Booked"
        SlotStatus.CANCELED -> "Canceled"
        SlotStatus.COMPLETED -> "Completed"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        border = BorderStroke(
            1.dp,
            if (slot.status == SlotStatus.AVAILABLE) MedAITheme.colors.status.success.copy(alpha = 0.3f)
            else if (slot.status == SlotStatus.BOOKED) MedAITheme.colors.primary.copy(alpha = 0.3f)
            else MedAITheme.colors.neutral.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Time slot duration range
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = MedAITheme.colors.text.secondary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${slot.startTime.take(5)} - ${slot.endTime.take(5)}",
                        style = MedAITheme.textStyle.body.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary
                    )
                }

                // Themed Status Badge label
                Box(
                    modifier = Modifier
                        .background(statusBgColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        style = MedAITheme.textStyle.label.small,
                        fontWeight = FontWeight.Bold,
                        color = statusColor
                    )
                }
            }

            // Quick Delete Button (Only active for AVAILABLE slots)
            if (slot.status == SlotStatus.AVAILABLE) {
                IconButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier
                        .size(32.dp)
                        .background(MedAITheme.colors.status.errorContainer, CircleShape)
                ) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete slot",
                        tint = MedAITheme.colors.status.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else if (slot.status == SlotStatus.BOOKED) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Booked",
                    tint = MedAITheme.colors.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Slot", style = MedAITheme.textStyle.headline.small) },
            text = {
                Text(
                    text = "Are you sure you want to remove the slot ${slot.startTime.take(5)} - ${slot.endTime.take(5)}?",
                    style = MedAITheme.textStyle.body.medium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false; onDelete() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MedAITheme.colors.status.error)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel", color = MedAITheme.colors.text.secondary)
                }
            }
        )
    }
}

@Composable
fun EmptySlotContent(
    onApplyTemplate: () -> Unit,
    onCreateSlot: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = MedAITheme.colors.primary.copy(alpha = 0.4f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = "No Slots Scheduled",
                style = MedAITheme.textStyle.title.large,
                fontWeight = FontWeight.Bold,
                color = MedAITheme.colors.text.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Generate daily duty hours automatically from shifts templates or define a single custom time slot.",
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.secondary,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onApplyTemplate,
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MedAITheme.colors.primary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MedAITheme.colors.primary)
                ) {
                    Text("Apply Template", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onCreateSlot,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MedAITheme.colors.primary,
                        contentColor = MedAITheme.colors.onPrimary
                    )
                ) {
                    Text("Add Slot", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
