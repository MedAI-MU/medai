package org.example.project.domain.audio

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

actual class PermissionHandler(private val context: Context) {
    actual fun hasMicrophonePermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    actual fun requestMicrophonePermission(onResult: (Boolean) -> Unit) {
        if (hasMicrophonePermission()) {
            onResult(true)
        } else {
            // Android requests permissions via Activity/ActivityResultLauncher in Compose.
            // We pass false here, and let the Compose UI launcher perform the request.
            onResult(false)
        }
    }
}
