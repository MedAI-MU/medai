package org.example.project.domain.audio

import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted

actual class PermissionHandler {
    actual fun hasMicrophonePermission(): Boolean {
        val status = AVAudioSession.sharedInstance().recordPermission
        return status == AVAudioSessionRecordPermissionGranted
    }

    actual fun requestMicrophonePermission(onResult: (Boolean) -> Unit) {
        AVAudioSession.sharedInstance().requestRecordPermission { granted ->
            onResult(granted)
        }
    }
}
