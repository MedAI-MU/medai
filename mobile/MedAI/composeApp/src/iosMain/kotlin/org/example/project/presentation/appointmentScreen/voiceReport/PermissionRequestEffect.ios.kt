package org.example.project.presentation.appointmentScreen.voiceReport

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import org.example.project.domain.audio.PermissionHandler

@Composable
actual fun PermissionRequestEffect(
    viewModel: VoiceReportViewModel,
    permissionHandler: PermissionHandler
) {
    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            if (effect is VoiceReportEffect.RequestMicrophonePermission) {
                permissionHandler.requestMicrophonePermission { granted ->
                    viewModel.onEvent(VoiceReportEvent.PermissionResult(granted))
                }
            }
        }
    }
}
