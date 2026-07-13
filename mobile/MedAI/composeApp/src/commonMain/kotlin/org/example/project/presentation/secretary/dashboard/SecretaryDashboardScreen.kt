package org.example.project.presentation.secretary.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.theme.LocalDimensions
import org.example.project.design_system.theme.MedAITheme
import org.example.project.design_system.component.image.MedAIAsyncImage
import org.example.project.domain.model.secretary.QueueEntry
import org.example.project.domain.model.secretary.QueueStatus
import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen
import org.example.project.presentation.patientDirectory.PatientsDirectoryScreen
import org.example.project.presentation.secretary.billing.BillingScreen
import org.example.project.presentation.secretary.doctors.SecretaryDoctorListScreen
import org.example.project.presentation.secretary.patient.PatientManagementScreen
import org.example.project.presentation.secretary.queue.QueueManagementScreen
import org.example.project.presentation.secretary.schedule.DailyScheduleSummaryScreen

class SecretaryDashboardScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow.parent ?: LocalNavigator.currentOrThrow
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

        // Get system current date
        val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val dayName = today.dayOfWeek.name.lowercase().replaceFirstChar { it.uppercase() }
        val monthName = today.month.name.lowercase().replaceFirstChar { it.uppercase() }
        val dateString = "$dayName, $monthName ${today.dayOfMonth}"

        MedAIScaffold(
            containerColor = MedAITheme.colors.background,
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = dimensions.medium),
                verticalArrangement = Arrangement.spacedBy(dimensions.large)
            ) {
                // Spacer at top
                item { Spacer(modifier = Modifier.height(dimensions.small)) }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                        ) {
                            MedAIAsyncImage(
                                imageUrl = state.avatarUrl,
                                nameForInitials = state.secretaryName.ifBlank { "Secretary" },
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                            )
                            Column {
                                Text(
                                    text = "Good Morning, ${state.secretaryName.ifBlank { "Secretary" }}",
                                    style = MedAITheme.textStyle.headline.small,
                                    fontWeight = FontWeight.Bold,
                                    color = MedAITheme.colors.text.primary
                                )
                                Text(
                                    text = dateString,
                                    style = MedAITheme.textStyle.body.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }
                        IconButton(
                            onClick = { /* Handle Notifications */ },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MedAITheme.colors.surface)
                                .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.1f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MedAITheme.colors.primary
                            )
                        }
                    }
                }

                // 2. Pulse Metrics Cards Grid (2x2)
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.medium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                        ) {
                            PulseCard(
                                title = "Pending Queue",
                                value = state.clinicStats.pendingAppointments.toString(),
                                icon = Icons.Default.Schedule,
                                tintColor = MedAITheme.colors.status.warning,
                                modifier = Modifier.weight(1f)
                            )
                            PulseCard(
                                title = "Checked In",
                                value = state.clinicStats.totalPatientsToday.toString(),
                                icon = Icons.Default.Group,
                                tintColor = MedAITheme.colors.primary,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                        ) {
                            PulseCard(
                                title = "On Duty",
                                value = state.clinicStats.activeDoctors.toString(),
                                icon = Icons.Default.LocalHospital,
                                tintColor = MedAITheme.colors.status.success,
                                modifier = Modifier.weight(1f)
                            )
//                            PulseCard(
//                                title = "Today's Revenue",
//                                value = "$${state.clinicStats.totalRevenueToday.toInt()}",
//                                icon = Icons.Default.AttachMoney,
//                                tintColor = Color(0xFFFFB300),
//                                modifier = Modifier.weight(1f)
//                            )
                        }
                    }
                }

                // 3. Quick Actions Header
                item {
                    Text(
                        text = "Quick Operations",
                        style = MedAITheme.textStyle.title.medium,
                        fontWeight = FontWeight.Bold,
                        color = MedAITheme.colors.text.primary
                    )
                }

                // 4. Quick Actions Panel Grid
                item {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(dimensions.medium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                        ) {

                            ActionCard(
                                title = "Doctor List",
                                desc = "Duty schedules",
                                icon = Icons.Default.CalendarMonth,
                                color = Color(0xFFFF6D00),
                                modifier = Modifier.weight(1f),
                                onClick = { navigator.push(SecretaryDoctorListScreen()) }
                            )

//                            ActionCard(
//                                title = "Register Patient",
//                                desc = "New profile",
//                                icon = Icons.Default.PersonAdd,
//                                color = MedAITheme.colors.primary,
//                                modifier = Modifier.weight(1f),
//                                onClick = { navigator.push(PatientManagementScreen()) }
//                            )
                            ActionCard(
                                title = "Patients Database",
                                desc = "Search directory",
                                icon = Icons.Default.Group,
                                color = Color(0xFF00B0FF),
                                modifier = Modifier.weight(1f),
                                onClick = { navigator.push(PatientsDirectoryScreen()) }
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
                        ) {
                            ActionCard(
                                title = "Check-In Desk",
                                desc = "Waiting room",
                                icon = Icons.Default.AssignmentTurnedIn,
                                color = Color(0xFF7C4DFF),
                                modifier = Modifier.weight(1f),
                                onClick = { navigator.push(QueueManagementScreen()) }
                            )
//                            ActionCard(
//                                title = "Billing & Payments",
//                                desc = "Invoices & logs",
//                                icon = Icons.Default.ReceiptLong,
//                                color = MedAITheme.colors.status.success,
//                                modifier = Modifier.weight(1f),
//                                onClick = { navigator.push(BillingScreen()) }
//                            )


                            ActionCard(
                                title = "Schedule Summary",
                                desc = "Daily patient counts",
                                icon = Icons.Default.CalendarMonth,
                                color = Color(0xFFE91E63),
                                modifier = Modifier.weight(1f),
                                onClick = { navigator.push(DailyScheduleSummaryScreen()) }
                            )
                        }
//                        Row(
//                            modifier = Modifier.fillMaxWidth(),
//                            horizontalArrangement = Arrangement.spacedBy(dimensions.medium)
//                        ) {
//
//                        }
                    }
                }

                // 5. Live Queue Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Queue Status",
                            style = MedAITheme.textStyle.title.medium,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.text.primary
                        )
                        Text(
                            text = "Live",
                            style = MedAITheme.textStyle.label.medium,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.status.success,
                            modifier = Modifier
                                .background(MedAITheme.colors.status.success.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                // 6. Live Queue List Content
                if (state.isLoading) {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = MedAITheme.colors.primary)
                        }
                    }
                } else if (state.queues.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(dimensions.radiusLarge))
                                .background(MedAITheme.colors.surface)
                                .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.05f), RoundedCornerShape(dimensions.radiusLarge)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = MedAITheme.colors.text.secondary.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(dimensions.small))
                                Text(
                                    text = "No patients checked in today",
                                    style = MedAITheme.textStyle.body.medium,
                                    color = MedAITheme.colors.text.secondary
                                )
                            }
                        }
                    }
                } else {
                    items(state.queues) { entry ->
                        QueueItemCard(entry = entry, onClick = {
                            navigator.push(DoctorPatientRecordsScreen(entry.patientId))
                        })
                    }
                }

                // Bottom padding spacer
                item { Spacer(modifier = Modifier.height(dimensions.large)) }
            }
        }
    }

    @Composable
    fun PulseCard(
        title: String,
        value: String,
        icon: ImageVector,
        tintColor: Color,
        modifier: Modifier = Modifier
    ) {
        val dimensions = LocalDimensions.current
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(dimensions.radiusLarge))
                .background(MedAITheme.colors.surface)
                .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.05f), RoundedCornerShape(dimensions.radiusLarge))
                .padding(dimensions.medium)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(tintColor.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(dimensions.medium))
                Text(
                    text = value,
                    style = MedAITheme.textStyle.headline.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )
                Text(
                    text = title,
                    style = MedAITheme.textStyle.body.small,
                    color = MedAITheme.colors.text.secondary
                )
            }
        }
    }

    @Composable
    fun ActionCard(
        title: String,
        desc: String,
        icon: ImageVector,
        color: Color,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val dimensions = LocalDimensions.current
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(dimensions.radiusLarge))
                .background(MedAITheme.colors.surface)
                .border(1.dp, MedAITheme.colors.primary.copy(alpha = 0.05f), RoundedCornerShape(dimensions.radiusLarge))
                .clickable { onClick() }
                .padding(dimensions.medium)
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(color.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(dimensions.medium))
                Text(
                    text = title,
                    style = MedAITheme.textStyle.body.medium,
                    fontWeight = FontWeight.Bold,
                    color = MedAITheme.colors.text.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = desc,
                        style = MedAITheme.textStyle.body.small,
                        color = MedAITheme.colors.text.secondary
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    @Composable
    fun QueueItemCard(entry: QueueEntry, onClick: () -> Unit) {
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
                            text = entry.patientName.take(1).uppercase(),
                            style = MedAITheme.textStyle.body.large,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.primary
                        )
                    }
                    Spacer(modifier = Modifier.width(dimensions.medium))
                    Column {
                        Text(
                            text = entry.patientName,
                            style = MedAITheme.textStyle.body.large,
                            fontWeight = FontWeight.Bold,
                            color = MedAITheme.colors.text.primary
                        )
                        Text(
                            text = "Assigned: ${entry.doctorName}",
                            style = MedAITheme.textStyle.body.small,
                            color = MedAITheme.colors.text.secondary
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    QueueStatusBadgeCard(entry.status, entry.isPast)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entry.appointmentTime.split("T").lastOrNull()?.take(5) ?: "",
                        style = MedAITheme.textStyle.label.small,
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }
        }
    }

    @Composable
    fun QueueStatusBadgeCard(status: QueueStatus, isPast: Boolean = false) {
        val dimensions = LocalDimensions.current
        val (color, text) = when (status) {
            QueueStatus.WAITING -> {
                if (isPast) {
                    MedAITheme.colors.status.error to "Overdue"
                } else {
                    MedAITheme.colors.status.warning to "Pending Approval"
                }
            }
            QueueStatus.IN_PROGRESS -> MedAITheme.colors.primary to "Approved"
            QueueStatus.COMPLETED -> MedAITheme.colors.status.success to "Completed"
            QueueStatus.CANCELLED -> MedAITheme.colors.status.error to "Rejected"
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

    @Composable
    fun QueueStatusBadge(status: QueueStatus, isPast: Boolean = false) {
        QueueStatusBadgeCard(status, isPast)
    }
}
