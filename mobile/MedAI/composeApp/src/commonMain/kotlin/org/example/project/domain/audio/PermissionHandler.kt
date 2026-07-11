package org.example.project.domain.audio

expect class PermissionHandler {
    fun hasMicrophonePermission(): Boolean
    fun requestMicrophonePermission(onResult: (Boolean) -> Unit)
}
