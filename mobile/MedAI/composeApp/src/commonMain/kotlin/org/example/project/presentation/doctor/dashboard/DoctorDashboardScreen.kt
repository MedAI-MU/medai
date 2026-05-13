package org.example.project.presentation.doctor.dashboard

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.dayPicker.MedAIDateCard
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.TimeZone
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.appBar.MedAiAppBar
import org.example.project.domain.model.appointment.AppointmentDetailStatus

import androidx.compose.runtime.LaunchedEffect

class DoctorDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = getScreenModel<DoctorDashboardViewModel>()
        val state by viewModel.uiState.collectAsState()
        val selectedDate by viewModel.selectedDate.collectAsState()
        val dates = viewModel.dates

        // Refresh data when screen enters composition (e.g. navigating back)
        LaunchedEffect(Unit) {
            viewModel.refresh()
        }

        MedAIScaffold(
            topBar = {
                Column {
                    MedAiAppBar(
                        title = "Doctor Dashboard",
                        centerTitle = false // Align start like the original design
                    )
                    Column(modifier = Modifier.padding(horizontal = 16.dp).padding(bottom = 16.dp)) {
                        Text(
                            text = "Your appointments for today",
                            style = MedAITheme.textStyle.body.large,
                            color = MedAITheme.colors.text.secondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(dates) { date ->
                                // Simple logic to convert LocalDate to epoch for comparison
                                // In real app, consider TimeZones strictly
                                val dateEpoch = date.atStartOfDayIn(TimeZone.currentSystemDefault()).toEpochMilliseconds()

                                MedAIDateCard(
                                    day = date.dayOfMonth.toString(),
                                    weekday = date.dayOfWeek.name.take(3),
                                    isSelected = dateEpoch == selectedDate, // Simplified equality check
                                    hasAppointment = false, // Could be dynamic
                                    onClick = { viewModel.onDateSelected(dateEpoch) }
                                )
                            }
                        }
                    }
                }
            },
            // Reverting to default behavior for safer insets handling unless specifically needed otherwise
            contentWindowInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top),
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {
                when (val uiState = state) {
                    is DoctorDashboardUiState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                    is DoctorDashboardUiState.Success -> {
                        DashboardContent(uiState.appointments)
                    }
                    is DoctorDashboardUiState.Error -> {
                        Text(
                            text = uiState.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardContent(appointments: List<AppointmentDetail>) {
    val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
    val total = appointments.size
    val pending = appointments.count { it.status == AppointmentDetailStatus.UPCOMING }
    val finished = appointments.count { it.status == AppointmentDetailStatus.FINISHED }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Quick Actions
        Text("Quick Actions", style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
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
            ActionButton(
                text = "Patients",
                icon = Icons.Default.Person,
                onClick = { navigator.push(org.example.project.presentation.patientDirectory.PatientsDirectoryScreen()) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Cards
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatsCard(label = "Total", count = total, modifier = Modifier.weight(1f))
            StatsCard(label = "Pending", count = pending, modifier = Modifier.weight(1f))
            StatsCard(label = "Finished", count = finished, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Today's Appointments", style = MedAITheme.textStyle.title.medium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        AppointmentList(appointments, modifier = Modifier.weight(1f))
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
        contentPadding = PaddingValues(top = 16.dp),
        modifier = modifier.fillMaxSize()
    ) {
        items(appointments) { appointment ->
            AppointmentCard(
                appointment = appointment,
                onClick = {
                    navigator.push(org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen(appointment.id))
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AppointmentCard(appointment: AppointmentDetail, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MedAITheme.colors.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = appointment.patientName,
                    style = MedAITheme.textStyle.title.medium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${appointment.date.time.hour}:${appointment.date.time.minute}",
                    style = MedAITheme.textStyle.body.medium,
                    color = MedAITheme.colors.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = appointment.doctorName,
                style = MedAITheme.textStyle.body.small,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = appointment.status.name,
                style = MedAITheme.textStyle.label.small,
                color = MedAITheme.colors.text.secondary
            )
        }
    }
}
