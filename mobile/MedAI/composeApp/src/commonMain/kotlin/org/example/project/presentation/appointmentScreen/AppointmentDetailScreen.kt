package org.example.project.presentation.appointmentScreen

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import org.example.project.presentation.appointmentScreen.voiceReport.VoiceReportViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.presentation.appointmentScreen.sheet.CancelAppointmentSheet
import org.example.project.presentation.appointmentScreen.component.UnifiedAppointmentHeaderCard
import org.example.project.presentation.doctor.records.DoctorPatientRecordsScreen

class AppointmentDetailScreen(val appointmentId: String) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<AppointmentViewModel>()
        val state by viewModel.state.collectAsState()
        val voiceReportViewModel = koinScreenModel<VoiceReportViewModel> { parametersOf(appointmentId) }

        var showCancelSheet by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState()

        LaunchedEffect(Unit) {
            viewModel.effect.collectLatest { effect ->
                when(effect) {
                    is AppointmentEffect.CloseSheet -> {
                        showCancelSheet = false
                    }
                    is AppointmentEffect.ShowToast -> {
                        // Ideally pass a SnackbarHostState to your Scaffold
                        // For now, assuming you handle it or rely on system toast
                        // println(effect.message)
                    }
                    is AppointmentEffect.NavigateBack -> {
                        navigator.pop()
                    }
                    is AppointmentEffect.NavigateToPatientRecords -> {
                        navigator.push(DoctorPatientRecordsScreen(effect.patientId))
                    }
                    else -> {}
                }
            }
        }

        LaunchedEffect(appointmentId) {
            viewModel.onEvent(AppointmentEvent.OnAppointmentClicked(appointmentId))
        }

        if (showCancelSheet) {
            CancelAppointmentSheet(
                onDismiss = { showCancelSheet = false },
                onConfirm = {
                    viewModel.onEvent(AppointmentEvent.OnConfirmCancel(appointmentId))
                },
                reasons = state.cancelReasons
            )
        }

        MedAIScaffold(
            title = "Appointment Details",
            onBackClick = { navigator.pop() }
        ) { padding ->
            if (state.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            } else {
                state.selectedAppointment?.let { appointment ->
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .fillMaxSize()
                            .padding(MedAITheme.dimensions.extraLarge)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Unified Doctor-Patient Header Card
                        UnifiedAppointmentHeaderCard(
                            appointment = appointment,
                            onViewPatientRecords = { patientId ->
                                viewModel.onEvent(AppointmentEvent.ViewPatientRecords(patientId))
                            }
                        )

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // AI Voice Consultation Report Section
                        org.example.project.presentation.appointmentScreen.voiceReport.VoiceReportSection(
                            viewModel = voiceReportViewModel
                        )

                        // Linked AI Medical Reports Section
                        if (state.linkedReports.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))
                            SectionTitle("Linked AI Medical Reports")
                            Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                            Column(verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)) {
                                state.linkedReports.forEach { report ->
                                    LinkedReportItem(report = report) {
                                        navigator.push(org.example.project.presentation.reportAnalysis.ReportAnalysisResultsScreen(report))
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                        // Only show Cancel/Reschedule if Upcoming and not in the past
                        if (appointment.status == AppointmentDetailStatus.UPCOMING && !appointment.isPast) {
                            MedAIButton(
                                text = "Cancel Appointment",
                                onClick = { showCancelSheet = true },
                                variant = ButtonVariant.Secondary,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun SectionTitle(text: String) {
        MedAIText(text, style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold))
    }

    @Composable
    private fun LinkedReportItem(
        report: org.example.project.domain.model.report_analysis.ReportAnalysis,
        onClick: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(MedAITheme.dimensions.radiusMedium),
            colors = CardDefaults.cardColors(
                containerColor = MedAITheme.colors.surface
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                MedAITheme.colors.neutral.copy(alpha = 0.08f)
            )
        ) {
            Row(
                modifier = Modifier.padding(MedAITheme.dimensions.large),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(MedAITheme.dimensions.medium))
                    Column {
                        val fileName = report.path.split("/").lastOrNull() ?: "report.pdf"
                        MedAIText(fileName, style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.Bold))
                        MedAIText("Analyzed: ${report.createdAt.take(10)}", style = MedAITheme.textStyle.body.small, color = MedAITheme.colors.text.secondary)
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MedAITheme.colors.text.secondary
                )
            }
        }
    }
}
