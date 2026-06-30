package org.example.project.presentation.secretary.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.example.project.design_system.component.button.MedAICardButton
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus

import org.example.project.presentation.secretary.patient.PatientManagementScreen
import org.example.project.presentation.secretary.queue.QueueManagementScreen
import org.example.project.presentation.secretary.billing.BillingScreen
import org.example.project.presentation.patientDirectory.PatientsDirectoryScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.example.project.design_system.theme.LocalDimensions

class SecretaryDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = koinScreenModel<SecretaryDashboardViewModel>()
        val state by viewModel.state.collectAsState()
        val dimensions = LocalDimensions.current

        val snackbarHostState = remember { SnackbarHostState() }

        LaunchedEffect(viewModel.effect) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is SecretaryDashboardEffect.ShowSnackbar -> {
                        snackbarHostState.showSnackbar(effect.message)
                    }
                }
            }
        }

        Scaffold(
            containerColor = MedAITheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(dimensions.medium)
            ) {
                // Header
                Text(
                    text = "Secretary Dashboard",
                    style = MedAITheme.textStyle.headline.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(dimensions.large))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard("Patients", state.clinicStats.totalPatientsToday.toString())
                    StatCard("Doctors", state.clinicStats.activeDoctors.toString())
                    StatCard("Revenue", "${state.clinicStats.totalRevenueToday}")
                }
                Spacer(modifier = Modifier.height(dimensions.large))

                // Quick Actions
                Text(
                    text = "Quick Actions",
                    style = MedAITheme.textStyle.title.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(dimensions.medium))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(dimensions.medium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MedAICardButton(
                            text = "Patients",
                            icon = Icons.Default.Group,
                            onClick = { navigator.push(PatientsDirectoryScreen()) })
                        MedAICardButton(
                            text = "New Patient",
                            icon = Icons.Default.Add,
                            onClick = { navigator.push(PatientManagementScreen()) })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        MedAICardButton(
                            text = "Check-In",
                            icon = Icons.Default.Schedule,
                            onClick = { navigator.push(QueueManagementScreen()) })
                        MedAICardButton(
                            text = "Billing",
                            icon = Icons.Default.AttachMoney,
                            onClick = { navigator.push(BillingScreen()) })
                    }
                }
                Spacer(modifier = Modifier.height(dimensions.large))

                // Queue / Appointments Lists
                Text(
                    text = "Current Queue",
                    style = MedAITheme.textStyle.title.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(dimensions.medium))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(dimensions.small)
                    ) {
                        items(state.queues) { entry ->
                            QueueItem(entry, onClick = { navigator.push(DoctorPatientRecordsScreen(entry.patientId)) })
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StatCard(label: String, value: String) {
        val dimensions = LocalDimensions.current
        Column(
            modifier = Modifier
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusMedium))
                .padding(dimensions.medium),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MedAITheme.textStyle.headline.small, color = MedAITheme.colors.primary, fontWeight = FontWeight.Bold)
            Text(text = label, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
        }
    }

    @Composable
    fun QueueItem(entry: QueueEntry, onClick: () -> Unit) {
        val dimensions = LocalDimensions.current
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusMedium))
                .clickable { onClick() }
                .padding(dimensions.medium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = entry.patientName, style = MedAITheme.textStyle.body.large, fontWeight = FontWeight.Bold, color = MedAITheme.colors.text.primary)
                Text(text = entry.doctorName, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
            }

            QueueStatusBadge(entry.status)
        }
    }

    @Composable
    fun QueueStatusBadge(status: QueueStatus) {
        val dimensions = LocalDimensions.current
        val (color, text) = when (status) {
            QueueStatus.WAITING -> MedAITheme.colors.status.warning to "Waiting"
            QueueStatus.IN_PROGRESS -> MedAITheme.colors.primary to "In Progress"
            QueueStatus.COMPLETED -> MedAITheme.colors.status.success to "Completed"
            QueueStatus.CANCELLED -> MedAITheme.colors.status.error to "Cancelled"
        }

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.RoundedCornerShape(dimensions.radiusSmall))
                .padding(horizontal = dimensions.small, vertical = dimensions.extraSmall)
        ) {
            Text(text = text, style = MedAITheme.textStyle.label.small, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
