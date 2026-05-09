package org.example.project.domain.services

interface AudioRecorder {
    fun startRecording()
    fun stopRecording(): ByteArray?
    fun isRecording(): Boolean
}
