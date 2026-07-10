package org.example.project.presentation.secretary.schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.LocalDate
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen

class DailyScheduleSummaryScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<DailyScheduleSummaryViewModel>()
        val state by viewModel.state.collectAsState()
        val dimensions = LocalDimensions.current
        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is DailyScheduleSummaryEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Daily Schedule Summary", style = MedAITheme.textStyle.headline.small) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MedAITheme.colors.primary)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MedAITheme.colors.background,
                        titleContentColor = MedAITheme.colors.text.primary
                    )
                )
            },
            containerColor = MedAITheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = dimensions.medium)
            ) {
                // Local state for controlling DatePicker visibility
                var showDatePicker by remember { mutableStateOf(false) }

                // 1. Horizontal Date ribbon Header
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = dimensions.small),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Select Date",
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary
                    )
                    IconButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MedAITheme.colors.primary.copy(alpha = 0.1f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select custom date",
                            tint = MedAITheme.colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // DatePickerDialog Implementation
                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = Clock.System.now().toEpochMilliseconds()
                    )
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            Text(
                                text = "Select",
                                modifier = Modifier
                                    .clickable {
                                        datePickerState.selectedDateMillis?.let { millis ->
                                            val localDate = Instant.fromEpochMilliseconds(millis)
                                                .toLocalDateTime(TimeZone.UTC).date
                                            viewModel.onEvent(DailyScheduleSummaryEvent.SelectDate(localDate))
                                        }
                                        showDatePicker = false
                                    }
                                    .padding(dimensions.medium),
                                style = MedAITheme.textStyle.label.large,
                                fontWeight = FontWeight.Bold,
                                color = MedAITheme.colors.primary
                            )
                        },
                        dismissButton = {
                            Text(
                                text = "Cancel",
                                modifier = Modifier
                                    .clickable { showDatePicker = false }
                                    .padding(dimensions.medium),
                                style = MedAITheme.textStyle.label.large,
                                color = MedAITheme.colors.text.secondary
                            )
                        }
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                if (state.isLoading && state.daySummaries.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(dimensions.small),
                        contentPadding = PaddingValues(bottom = dimensions.medium),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.daySummaries) { summary ->
                            DateCard(
                                summary = summary,
                                isSelected = summary.date == state.selectedDate,
                                onClick = { viewModel.onEvent(DailyScheduleSummaryEvent.SelectDate(summary.date)) }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimensions.small))

                // 2. Patient details header
                Text(
                    text = "Scheduled Patients for ${formatDateLabel(state.selectedDate)}",
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary,
                    modifier = Modifier.padding(bottom = dimensions.small)
                )

                // 3. Patients list
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else if (state.filteredAppointments.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(vertical = dimensions.large)
                            .clip(RoundedCornerShape(dimensions.radiusLarge))
                            .background(MedAITheme.colors.surface)
                            .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.05f), RoundedCornerShape(dimensions.radiusLarge)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(dimensions.large)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = MedAITheme.colors.text.secondary.copy(alpha = 0.4f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(dimensions.medium))
                            Text(
                                text = "No Appointments Scheduled",
                                style = MedAITheme.textStyle.title.medium,
                                fontWeight = FontWeight.Bold,
                                color = MedAITheme.colors.text.primary
                            )
                            Spacer(modifier = Modifier.height(dimensions.small))
                            Text(
                                text = "There are no patients scheduled for this date.",
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.text.secondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensions.small),
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        items(state.filteredAppointments) { appointment ->
                            AppointmentItemCard(
                                appointment = appointment,
                                onClick = {
                                    navigator.push(DoctorPatientRecordsScreen(appointment.patientId))
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(dimensions.large)) }
                    }
                }
            }
        }
    }

    @Composable
    fun DateCard(
        summary: DayScheduleSummary,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        val dimensions = LocalDimensions.current
        val dayAbbr = summary.date.dayOfWeek.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        val monthAbbr = summary.date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }

        val backgroundModifier = if (isSelected) {
            Modifier.background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MedAITheme.colors.primary,
                        MedAITheme.colors.primary.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(dimensions.radiusLarge)
            )
        } else {
            Modifier.background(
                color = MedAITheme.colors.surface,
                shape = RoundedCornerShape(dimensions.radiusLarge)
            ).border(
                width = 1.dp,
                color = MedAITheme.colors.primary.copy(alpha = 0.08f),
                shape = RoundedCornerShape(dimensions.radiusLarge)
            )
        }

        Box(
            modifier = Modifier
                .width(86.dp)
                .clip(RoundedCornerShape(dimensions.radiusLarge))
                .then(backgroundModifier)
                .clickable { onClick() }
                .padding(vertical = 12.dp, horizontal = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayAbbr,
                    style = MedAITheme.textStyle.label.medium,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = summary.date.dayOfMonth.toString(),
                    style = MedAITheme.textStyle.headline.medium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else MedAITheme.colors.text.primary
                )
                Text(
                    text = monthAbbr,
                    style = MedAITheme.textStyle.label.small,
                    color = if (isSelected) Color.White.copy(alpha = 0.8f) else MedAITheme.colors.text.secondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .background(
                            color = if (isSelected) Color.White.copy(alpha = 0.2f) else MedAITheme.colors.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${summary.patientCount} Pts",
                        style = MedAITheme.textStyle.label.small,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else MedAITheme.colors.primary
                    )
                }
            }
        }
    }

    @Composable
    fun AppointmentItemCard(
        appointment: AppointmentDetail,
        onClick: () -> Unit
    ) {
        val dimensions = LocalDimensions.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(dimensions.radiusLarge))
                .background(MedAITheme.colors.surface)
                .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.05f), RoundedCornerShape(dimensions.radiusLarge))
                .clickable { onClick() }
                .padding(dimensions.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MedAITheme.colors.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = appointment.patientName.take(1).uppercase(),
                            style = MedAITheme.textStyle.body.large,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensions.medium))
                    Column {
                        Text(
                            text = appointment.patientName,
                            style = MedAITheme.textStyle.body.large,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.text.primary
                        )
                        Text(
                            text = "Assigned: ${appointment.doctorName}",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    AppointmentStatusBadge(appointment.status)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = appointment.date.time.toString().take(5),
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }
        }
    }

    @Composable
    fun AppointmentStatusBadge(status: AppointmentDetailStatus) {
        val (color, text) = when (status) {
            AppointmentDetailStatus.UPCOMING -> MedAITheme.colors.status.warning to "Scheduled"
            AppointmentDetailStatus.FINISHED -> MedAITheme.colors.status.success to "Finished"
            AppointmentDetailStatus.CANCELLED -> MedAITheme.colors.status.error to "Cancelled"
        }

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.12f), shape = RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = text,
                style = MedAITheme.textStyle.label.small,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }

    private fun formatDateLabel(date: LocalDate): String {
        val dayAbbr = date.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val monthAbbr = date.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
        return "$dayAbbr, $monthAbbr ${date.dayOfMonth}"
    }
}
