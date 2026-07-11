package org.example.project.domain.audio

interface AudioRecorder {
    fun startRecording()
    fun stopRecording(): ByteArray
    fun release()
}
