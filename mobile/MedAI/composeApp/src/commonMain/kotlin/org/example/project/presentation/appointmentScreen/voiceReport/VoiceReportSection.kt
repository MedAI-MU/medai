package org.example.project.presentation.appointmentScreen.voiceReport

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import org.example.project.design_system.component.button.ButtonVariant
import org.example.project.design_system.component.button.MedAIButton
import org.example.project.design_system.component.text.MedAIText
import org.example.project.design_system.theme.MedAITheme
import org.example.project.domain.model.auth.UserRole
import org.example.project.domain.repository.auth.UserSessionManager
import org.example.project.domain.model.voice_report.VoiceReportStatus
import org.example.project.presentation.appointmentScreen.voiceReport.component.ClinicalReportCard
import org.example.project.presentation.appointmentScreen.voiceReport.sheet.VoiceReportDetailsSheet
import org.example.project.presentation.appointmentScreen.voiceReport.component.IdlePromptUi
import org.example.project.presentation.appointmentScreen.voiceReport.component.ProcessingUi
import org.example.project.presentation.appointmentScreen.voiceReport.component.RecordingUi
import org.example.project.domain.audio.PermissionHandler
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceReportSection(
    viewModel: VoiceReportViewModel,
    modifier: Modifier = Modifier
) {
    // 1. Role Gating Check
    val sessionManager: UserSessionManager = koinInject()
    var userRole by remember { mutableStateOf<UserRole?>(null) }

    LaunchedEffect(Unit) {
        userRole = sessionManager.getUserRole()
    }

    if (userRole == UserRole.DOCTOR || userRole == UserRole.SECRETARY) {
        VoiceReportSectionContent(
            viewModel = viewModel,
            modifier = modifier
        )
    }
}

@Composable
private fun VoiceReportSectionContent(
    viewModel: VoiceReportViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val permissionHandler: PermissionHandler = koinInject()
    PermissionRequestEffect(viewModel, permissionHandler)

    var showHistory by remember { mutableStateOf(false) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is VoiceReportEffect.ShowToast -> {
                    toastMessage = effect.message
                }
                VoiceReportEffect.RequestMicrophonePermission -> {
                    // Handled implicitly via mock/real fallbacks, but shown to the user if needed
                    toastMessage = "Microphone access requested."
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.onEvent(VoiceReportEvent.Refresh)
    }

    // Quick custom toast visualizer since standard Voyager screens might not have Snackbar hosts
    toastMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(3000)
            toastMessage = null
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusLarge))
            .background(MedAITheme.colors.neutral.copy(alpha = 0.02f))
            .border(
                width = 1.dp,
                color = MedAITheme.colors.neutral.copy(alpha = 0.08f),
                shape = RoundedCornerShape(MedAITheme.dimensions.radiusLarge)
            )
            .padding(MedAITheme.dimensions.large)
    ) {
        // Section Title & Toast
        Box(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.KeyboardVoice,
                        contentDescription = "Voice Reports Icon",
                        tint = MedAITheme.colors.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                    MedAIText(
                        text = "AI Voice consultation",
                        style = MedAITheme.textStyle.title.medium.copy(fontWeight = FontWeight.Bold),
                        color = MedAITheme.colors.text.primary
                    )
                }

                if (state.reports.isNotEmpty()) {
                    IconButton(onClick = { viewModel.onEvent(VoiceReportEvent.Refresh) }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh reports",
                            tint = MedAITheme.colors.primary
                        )
                    }
                }
            }

            // Animated inline toast message
            androidx.compose.animation.AnimatedVisibility(
                visible = toastMessage != null,
                modifier = Modifier.align(Alignment.Center)
            ) {
                toastMessage?.let {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusRound))
                            .background(MedAITheme.colors.neutral.copy(alpha = 0.85f))
                            .padding(horizontal = MedAITheme.dimensions.medium, vertical = 6.dp)
                    ) {
                        MedAIText(
                            text = it,
                            style = MedAITheme.textStyle.body.small,
                            color = Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

        // 2. State Phase Machine UI switcher
        if (state.isUploading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = MedAITheme.colors.primary)
                    Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))
                    MedAIText(
                        text = "Uploading Audio...",
                        style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.SemiBold),
                        color = MedAITheme.colors.text.secondary
                    )
                }
            }
        } else {
            when (val phase = state.phase) {
                VoiceReportPhase.Idle -> {
                    if (state.permissionDenied) {
                        PermissionDeniedCard(viewModel = viewModel)
                    } else {
                        IdlePromptUi(
                            onStartClick = { viewModel.onEvent(VoiceReportEvent.StartRecording) }
                        )
                    }
                }
                VoiceReportPhase.Recording -> {
                    RecordingUi(
                        durationSeconds = state.recordingDurationSeconds,
                        onStopClick = { viewModel.onEvent(VoiceReportEvent.StopAndUpload) },
                        onCancelClick = { viewModel.onEvent(VoiceReportEvent.CancelRecording) }
                    )
                }
                is VoiceReportPhase.Processing -> {
                    ProcessingUi()
                }
                is VoiceReportPhase.Success -> {
                    ClinicalReportCard(
                        report = phase.report,
                        modifier = Modifier.clickable {
                            viewModel.onEvent(VoiceReportEvent.OnReportClicked(phase.report))
                        }
                    )
                }
                is VoiceReportPhase.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                            .background(MedAITheme.colors.status.errorContainer)
                            .padding(MedAITheme.dimensions.large)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = "Error icon",
                                    tint = MedAITheme.colors.status.error,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                                MedAIText(
                                    text = "Processing Failed",
                                    style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold),
                                    color = MedAITheme.colors.status.error
                                )
                            }
                            Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
                            MedAIText(
                                text = phase.message,
                                style = MedAITheme.textStyle.body.medium,
                                color = MedAITheme.colors.text.primary,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.medium)
                            ) {
                                MedAIButton(
                                    text = "Dismiss",
                                    onClick = { viewModel.onEvent(VoiceReportEvent.DismissError) },
                                    variant = ButtonVariant.Secondary,
                                    modifier = Modifier.weight(1f)
                                )
                                if (phase.isRetryable) {
                                    MedAIButton(
                                        text = "Retry",
                                        onClick = { viewModel.onEvent(VoiceReportEvent.RetryUpload) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. History Accordion
        if (state.reports.isNotEmpty()) {
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.large))
            Divider(color = MedAITheme.colors.neutral.copy(alpha = 0.08f))
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showHistory = !showHistory }
                    .padding(vertical = MedAITheme.dimensions.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History icon",
                        tint = MedAITheme.colors.text.secondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))
                    MedAIText(
                        text = "History (${state.reports.size} Reports)",
                        style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.SemiBold),
                        color = MedAITheme.colors.text.secondary
                    )
                }

                Icon(
                    imageVector = Icons.Default.ExpandMore,
                    contentDescription = "Show/Hide History",
                    tint = MedAITheme.colors.text.secondary,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(if (showHistory) 180f else 0f)
                )
            }

            AnimatedVisibility(visible = showHistory) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = MedAITheme.dimensions.small),
                    verticalArrangement = Arrangement.spacedBy(MedAITheme.dimensions.small)
                ) {
                    for (r in state.reports) {
                        val isCurrentReport = when (val p = state.phase) {
                            is VoiceReportPhase.Success -> p.report.id == r.id
                            is VoiceReportPhase.Processing -> p.reportId == r.id
                            else -> false
                        }

                        val borderMod = if (isCurrentReport) {
                            Modifier.border(
                                width = 1.5.dp,
                                color = MedAITheme.colors.primary,
                                shape = RoundedCornerShape(MedAITheme.dimensions.radiusMedium)
                            )
                        } else Modifier

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
                                .background(
                                    color = if (isCurrentReport) {
                                        MedAITheme.colors.primary.copy(alpha = 0.05f)
                                    } else {
                                        MedAITheme.colors.neutral.copy(alpha = 0.04f)
                                    }
                                )
                                .then(borderMod)
                                .clickable {
                                    viewModel.onEvent(VoiceReportEvent.ViewReport(r))
                                    viewModel.onEvent(VoiceReportEvent.OnReportClicked(r))
                                }
                                .padding(MedAITheme.dimensions.medium),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                MedAIText(
                                    text = "Report #${r.id.takeLast(4)}",
                                    style = MedAITheme.textStyle.body.medium.copy(fontWeight = FontWeight.SemiBold),
                                    color = MedAITheme.colors.text.primary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                MedAIText(
                                    text = r.createdAt.take(16).replace("T", " "),
                                    style = MedAITheme.textStyle.body.small,
                                    color = MedAITheme.colors.text.tertiary
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val statusText = r.status.name.lowercase().replaceFirstChar { it.uppercase() }
                                val statusColor = when (r.status) {
                                    VoiceReportStatus.COMPLETED -> MedAITheme.colors.status.completed
                                    VoiceReportStatus.PROCESSING -> MedAITheme.colors.status.waiting
                                    VoiceReportStatus.QUEUED -> MedAITheme.colors.status.waiting
                                    VoiceReportStatus.FAILED -> MedAITheme.colors.status.error
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(MedAITheme.dimensions.radiusSmall))
                                        .background(statusColor.copy(alpha = 0.12f))
                                        .padding(horizontal = MedAITheme.dimensions.small, vertical = 2.dp)
                                ) {
                                    MedAIText(
                                        text = statusText,
                                        style = MedAITheme.textStyle.label.small.copy(fontWeight = FontWeight.Bold),
                                        color = statusColor
                                    )
                                }

                                Spacer(modifier = Modifier.width(MedAITheme.dimensions.small))

                                IconButton(
                                    onClick = { viewModel.onEvent(VoiceReportEvent.DeleteReport(r.id)) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete report",
                                        tint = MedAITheme.colors.status.error.copy(alpha = 0.8f),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.selectedReportDetails != null) {
        VoiceReportDetailsSheet(
            report = state.selectedReportDetails!!,
            onDismiss = { viewModel.onEvent(VoiceReportEvent.DismissReportDetails) }
        )
    }
}

@Composable
private fun PermissionDeniedCard(
    viewModel: VoiceReportViewModel,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(MedAITheme.dimensions.radiusMedium))
            .background(MedAITheme.colors.status.errorContainer.copy(alpha = 0.1f))
            .border(
                width = 1.dp,
                color = MedAITheme.colors.status.error.copy(alpha = 0.4f),
                shape = RoundedCornerShape(MedAITheme.dimensions.radiusMedium)
            )
            .padding(MedAITheme.dimensions.large)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MicOff,
                contentDescription = "Microphone Access Required",
                tint = MedAITheme.colors.status.error,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
            MedAIText(
                text = "Microphone Access Required",
                style = MedAITheme.textStyle.title.small.copy(fontWeight = FontWeight.Bold),
                color = MedAITheme.colors.status.error
            )
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.small))
            MedAIText(
                text = "MedAI needs microphone access to record patient consultations for AI analysis. Please grant the permission to continue.",
                style = MedAITheme.textStyle.body.medium,
                color = MedAITheme.colors.text.secondary,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(MedAITheme.dimensions.medium))
            MedAIButton(
                text = "Grant Permission",
                onClick = { viewModel.onEvent(VoiceReportEvent.RequestPermission) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
