package org.example.project.presentation.appointmentScreen

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import org.example.project.presentation.appointmentScreen.voiceReport.VoiceReportViewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.flow.collectLatest
import org.example.project.core.presentation.util.toUiString
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.scaffold.MedAIScaffold
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.appointment.AppointmentDetail
import org.example.project.domain.model.appointment.AppointmentDetailStatus
import org.example.project.presentation.appointmentScreen.sheet.CancelAppointmentSheet

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
                        // 1. Doctor Profile Card
                        DoctorProfileSection(appointment)

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // 2. Schedule Info
                        SectionTitle("Scheduled Appointment")
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                        MedAIText(appointment.date.toUiString(), style = MedAITheme.textStyle.body.large)

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // 3. Patient Info
                        SectionTitle("Patient Information")
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                        InfoRow("Full Name", appointment.patientName)

                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraLarge))

                        // 4. AI Voice Consultation Report Section
                        org.example.project.presentation.appointmentScreen.voiceReport.VoiceReportSection(
                            viewModel = voiceReportViewModel
                        )

                        Spacer(modifier = Modifier.weight(1f))
                        Spacer(modifier = Modifier.height(MedAITheme.dimensions.extraExtraLarge))

                        // Only show Cancel/Reschedule if Upcoming
                        if (appointment.status == AppointmentDetailStatus.UPCOMING) {
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
    fun DoctorProfileSection(appointment: AppointmentDetail) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(MedAITheme.dimensions.spacing64).clip(CircleShape).background(Color.LightGray)
            )
            Spacer(modifier = Modifier.width(MedAITheme.dimensions.large))
            Column {
                MedAIText(appointment.doctorName, style = MedAITheme.textStyle.headline.small.copy(fontWeight = FontWeight.Bold))
                MedAIText(appointment.specialty, style = MedAITheme.textStyle.body.medium, color = MedAITheme.colors.text.secondary)
                MedAIText("★ ${appointment.doctorRating}", style = MedAITheme.textStyle.label.medium, color = MedAITheme.colors.primary)
            }
        }
    }

    @Composable
    fun SectionTitle(text: String) {
        MedAIText(text, style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold))
    }

    @Composable
    fun InfoRow(label: String, value: String) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = MedAITheme.dimensions.extraSmall),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MedAIText(label, color = MedAITheme.colors.text.secondary, style = MedAITheme.textStyle.body.medium)
            MedAIText(value, style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.SemiBold))
        }
    }
}
