package org.example.project.presentation.appointmentScreen.voiceReport

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.collectLatest
import org.example.project.domain.audio.PermissionHandler

@Composable
actual fun PermissionRequestEffect(
    viewModel: VoiceReportViewModel,
    permissionHandler: PermissionHandler
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onEvent(VoiceReportEvent.PermissionResult(isGranted))
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collectLatest { effect ->
            if (effect is VoiceReportEffect.RequestMicrophonePermission) {
                launcher.launch(android.Manifest.permission.RECORD_AUDIO)
            }
        }
    }
}
