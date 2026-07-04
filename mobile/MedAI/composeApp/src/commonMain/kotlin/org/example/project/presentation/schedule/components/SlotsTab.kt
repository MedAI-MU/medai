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


// ==================== Slots Tab ====================

@Composable
fun SlotsTab(
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
fun DayCard(
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
fun SlotRow(
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
