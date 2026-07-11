package org.example.project.presentation.appointmentScreen.voiceReport

import androidx.compose.runtime.Composable
import org.example.project.domain.audio.PermissionHandler

@Composable
expect fun PermissionRequestEffect(
    viewModel: VoiceReportViewModel,
    permissionHandler: PermissionHandler
)
