package org.example.project.domain.services

// Stub implementation for iOS for now to make project compile.
// A real implementation would use AVAudioRecorder.
class IOSAudioRecorder : AudioRecorder {
    override fun startRecording() {
        // Not yet implemented
    }

    override fun stopRecording(): ByteArray? {
        return ByteArray(0)
    }

    override fun isRecording(): Boolean = false
}
