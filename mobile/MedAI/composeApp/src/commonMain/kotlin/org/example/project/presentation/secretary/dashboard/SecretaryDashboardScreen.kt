package org.example.project.presentation.secretary.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import org.example.project.design_system.component.button.MedAICardButton
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus

import org.example.project.presentation.secretary.patient.PatientManagementScreen
import org.example.project.presentation.secretary.queue.QueueManagementScreen
import org.example.project.presentation.secretary.billing.BillingScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SecretaryDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<SecretaryDashboardViewModel>()
        val state by viewModel.state.collectAsState()

        Scaffold(
            containerColor = MedAITheme.colors.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
            ) {
                // ... (Header and Stats Row omitted for brevity, assuming they are unchanged or I need to be careful not to delete them)
                // Actually, I should just target the Quick Actions Row.

                // Header
                Text(
                    text = "Secretary Dashboard",
                    style = MedAITheme.textStyle.headline.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(24.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatCard("Patients", state.clinicStats.totalPatientsToday.toString())
                    StatCard("Doctors", state.clinicStats.activeDoctors.toString())
                    StatCard("Revenue", "$${state.clinicStats.totalRevenueToday}")
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Quick Actions
                Text(
                    text = "Quick Actions",
                    style = MedAITheme.textStyle.title.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    MedAICardButton(text = "New Patient", icon = Icons.Default.Add, onClick = { navigator.push(PatientManagementScreen()) })
                    MedAICardButton(text = "Check-In", icon = Icons.Default.Schedule, onClick = { navigator.push(QueueManagementScreen()) })
                    MedAICardButton(text = "Billing", icon = Icons.Default.AttachMoney, onClick = { navigator.push(BillingScreen()) })
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Queue / Appointments Lists
                Text(
                    text = "Current Queue",
                    style = MedAITheme.textStyle.title.medium,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MedAITheme.colors.primary)
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.queues) { entry ->
                            QueueItem(entry)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun StatCard(label: String, value: String) {
        Column(
            modifier = Modifier
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = value, style = MedAITheme.textStyle.headline.small, color = MedAITheme.colors.primary, fontWeight = FontWeight.Bold)
            Text(text = label, style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
        }
    }

    @Composable
    fun QueueItem(entry: QueueEntry) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MedAITheme.colors.surface, shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                .padding(16.dp),
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
        val (color, text) = when (status) {
            QueueStatus.WAITING -> Color(0xFFFFCC00) to "Waiting"
            QueueStatus.IN_PROGRESS -> Color(0xFF00E5FF) to "In Progress"
            QueueStatus.COMPLETED -> Color(0xFF00C853) to "Completed"
            QueueStatus.CANCELLED -> Color(0xFFFF5252) to "Cancelled"
        }

        Box(
            modifier = Modifier
                .background(color.copy(alpha = 0.2f), shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = text, style = MedAITheme.textStyle.label.small, color = color, fontWeight = FontWeight.Bold)
        }
    }
}
