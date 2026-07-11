package org.example.project.presentation.doctor.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.image.MedAIAsyncImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.domain.model.appointment.AppointmentDetailStatus

class DoctorDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DoctorDashboardViewModel>()
        val state by viewModel.state.collectAsState()

        // Refresh data when screen enters composition
        LaunchedEffect(Unit) {
            viewModel.onEvent(DoctorDashboardEvent.Refresh)
        }

        MedAIScaffold(
            topBar = {
                Column {
                    MedAiAppBar(
                        title = "Doctor Dashboard",
                        centerTitle = false,
                        actions = {
                            MedAIAsyncImage(
                                imageUrl = state.avatarUrl,
                                nameForInitials = state.doctorName.ifBlank { "Doctor" },
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(36.dp)
                                    .clip(CircleShape)
                            )
                        }
                    )
                    // PATIENT AESTHETIC: Tinted background for the calendar block
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp)
                    ) {
                        // Month label row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Your appointments",
                                style = MedAITheme.textStyle.body.large,
                                color = MedAITheme.colors.text.secondary
                            )
                            Text(
                                text = "${(state.displayedMonth?.month?.name ?: "").take(3)} ${(state.displayedMonth?.year ?: 0)}",
                                style = MedAITheme.textStyle.label.medium,
                                color = MedAITheme.colors.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        // Date strip with month navigation arrows
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { viewModel.onEvent(DoctorDashboardEvent.OnPreviousMonthClicked) }) {
                                Icon(
                                    Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Month",
                                    tint = MedAITheme.colors.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }

                            LazyRow(
                                modifier = Modifier.weight(1f),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(horizontal = 4.dp)
                            ) {
                                items(state.dates) { date ->
                                    val dateEpoch = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

                                    MedAIDateCard(
                                        day = date.dayOfMonth.toString(),
                                        weekday = date.dayOfWeek.name.take(3),
                                        isSelected = dateEpoch == state.selectedDate,
                                        hasAppointment = date in state.appointmentDates,
                                        onClick = { viewModel.onEvent(DoctorDashboardEvent.OnDateSelected(dateEpoch)) }
                                    )
                                }
                            }

                            IconButton(onClick = { viewModel.onEvent(DoctorDashboardEvent.OnNextMonthClicked) }) {
                                Icon(
                                    Icons.Default.ChevronRight,
                                    contentDescription = "Next Month",
                                    tint = MedAITheme.colors.primary,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                        }
                    }
                }
            },
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                val uiState = state.uiState
                DashboardContent(
                    allAppointments = if (uiState is DoctorDashboardUiState.Success) uiState.appointments else emptyList(),
                    filteredAppointments = state.filteredAppointments,
                    isLoading = uiState is DoctorDashboardUiState.Loading,
                    errorMessage = if (uiState is DoctorDashboardUiState.Error) uiState.message else null
                )
            }
        }
    }
}

@Composable
fun DashboardContent(
    allAppointments: List<AppointmentDetail>,
    filteredAppointments: List<AppointmentDetail>,
    isLoading: Boolean = false,
    errorMessage: String? = null
) {
    val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
    val total = allAppointments.size
    val pending = allAppointments.count { it.status == AppointmentDetailStatus.UPCOMING }
    val finished = allAppointments.count { it.status == AppointmentDetailStatus.FINISHED }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Quick Actions
        Text("Quick Actions", style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = "Consultations",
                    icon = Icons.Default.Chat,
                    onClick = { navigator.push(org.example.project.presentation.doctor.chat.DoctorChatListScreen()) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Prescriptions",
                    icon = Icons.Default.Description,
                    onClick = { navigator.push(org.example.project.presentation.doctor.prescription.EPrescriptionScreen()) },
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ActionButton(
                    text = "Patients",
                    icon = Icons.Default.Person,
                    onClick = { navigator.push(org.example.project.presentation.patientDirectory.PatientsDirectoryScreen()) },
                    modifier = Modifier.weight(1f)
                )
                ActionButton(
                    text = "Services",
                    icon = Icons.Default.MedicalServices,
                    onClick = { navigator.push(org.example.project.presentation.doctor.services.DoctorServicesScreen()) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatsCard(label = "Total", count = total, modifier = Modifier.weight(1f))
            StatsCard(label = "Pending", count = pending, modifier = Modifier.weight(1f))
            StatsCard(label = "Finished", count = finished, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section header for the selected day's appointments
        Text(
            text = "Appointments (${filteredAppointments.size})",
            style = MedAITheme.textStyle.title.medium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                }
            }
            errorMessage != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MedAIText(
                            text = errorMessage,
                            style = MedAITheme.textStyle.title.medium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        MedAIText(
                            text = "Please check your server connection",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary
                        )
                    }
                }
            }
            filteredAppointments.isNotEmpty() -> {
                AppointmentList(filteredAppointments, modifier = Modifier.weight(1f))
            }
            else -> {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MedAITheme.colors.primary.copy(alpha = 0.06f))
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        MedAIText(
                            text = "No appointments for this date",
                            style = MedAITheme.textStyle.title.medium,
                            color = MedAITheme.colors.text.secondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        MedAIText(
                            text = "Select a different date to view appointments",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.primary.copy(alpha = 0.1f)),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = MedAITheme.colors.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun StatsCard(label: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = count.toString(),
                style = MedAITheme.textStyle.headline.medium.copy(fontWeight = FontWeight.Bold),
                color = MedAITheme.colors.primary
            )
            Text(
                text = label,
                style = MedAITheme.textStyle.label.medium,
                color = MedAITheme.colors.text.secondary
            )
        }
    }
}

@Composable
fun AppointmentList(appointments: List<AppointmentDetail>, modifier: Modifier = Modifier) {
    val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
    LazyColumn(
        contentPadding = PaddingValues(top = 8.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = {
                    navigator.push(org.example.project.presentation.appointmentScreen.AppointmentDetailScreen(appointment.id))
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentDetail, onClick: () -> Unit) {
    // PATIENT AESTHETIC: Sleek row layout with status indicator dot
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MedAITheme.colors.surface)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Status Dot
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(
                    when (appointment.status) {
                        AppointmentDetailStatus.UPCOMING -> MedAITheme.colors.primary
                        AppointmentDetailStatus.FINISHED -> MedAITheme.colors.status.success // Green
                        AppointmentDetailStatus.CANCELLED -> MedAITheme.colors.status.error // Red
                    }
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = appointment.patientName,
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${appointment.date.time.hour.toString().padStart(2, '0')}:${appointment.date.time.minute.toString().padStart(2, '0')}",
                    style = MedAITheme.textStyle.body.large,
                    color = MedAITheme.colors.primary,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${appointment.status.name.lowercase().replaceFirstChar { it.uppercase() }}",
                style = MedAITheme.textStyle.label.medium,
                color = MedAITheme.colors.text.secondary
            )
        }
    }
}
