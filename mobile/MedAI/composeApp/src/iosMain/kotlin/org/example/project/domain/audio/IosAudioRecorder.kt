package org.example.project.domain.audio

import kotlinx.cinterop.*
import platform.AVFAudio.AVAudioQualityHigh
import platform.AVFAudio.AVAudioRecorder
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayAndRecord
import platform.AVFAudio.AVEncoderAudioQualityKey
import platform.AVFAudio.AVFormatIDKey
import platform.AVFAudio.AVNumberOfChannelsKey
import platform.AVFAudio.AVSampleRateKey
import platform.AVFAudio.setActive
import platform.AVFoundation.*
import platform.Foundation.*
import platform.CoreAudio.*
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class)
class IosAudioRecorder : AudioRecorder {

    private var recorder: AVAudioRecorder? = null
    private var tempFileURL: NSURL? = null

    override fun startRecording() {
        memScoped {
            val errorVar = alloc<ObjCObjectVar<NSError?>>()
            val audioSession = AVAudioSession.sharedInstance()

            // Set session category to PlayAndRecord to allow record input
            val successCategory = audioSession.setCategory(
                category = AVAudioSessionCategoryPlayAndRecord,
                error = errorVar.ptr
            )
            if (!successCategory) {
                val err = errorVar.value
                throw AudioRecordingException("Failed to set audio session category: ${err?.localizedDescription}")
            }

            val successActive = audioSession.setActive(true, error = errorVar.ptr)
            if (!successActive) {
                val err = errorVar.value
                throw AudioRecordingException("Failed to activate audio session: ${err?.localizedDescription}")
            }

            // Create temp file path
            val tempDir = NSTemporaryDirectory()
            val fileName = "temp_recording_${NSDate().timeIntervalSince1970.toLong()}.m4a"
            val filePath = tempDir + fileName
            val fileURL = NSURL.fileURLWithPath(filePath)
            tempFileURL = fileURL

            // Configure recording settings
            val settings = mapOf<Any?, Any?>(
                AVFormatIDKey to kAudioFormatMPEG4AAC,
                AVSampleRateKey to 44100.0,
                AVNumberOfChannelsKey to 1,
                AVEncoderAudioQualityKey to AVAudioQualityHigh
            )

            // Instantiate using positional arguments to avoid named parameter mismatches (uRL vs URL)
            val avRecorder = AVAudioRecorder(
                fileURL,
                settings,
                errorVar.ptr
            )
            val err = errorVar.value
            if (err != null) {
                tempFileURL = null
                throw AudioRecordingException("Failed to initialize AVAudioRecorder: ${err.localizedDescription}")
            }

            recorder = avRecorder
            avRecorder.prepareToRecord()
            val started = avRecorder.record()
            if (!started) {
                recorder = null
                tempFileURL = null
                throw AudioRecordingException("AVAudioRecorder failed to start recording.")
            }
        }
    }

    override fun stopRecording(): ByteArray {
        val avRecorder = recorder
        val fileURL = tempFileURL

        if (avRecorder == null || fileURL == null) {
            throw AudioRecordingException("No active audio recording to stop.")
        }

        try {
            avRecorder.stop()
        } catch (e: Exception) {
            throw AudioRecordingException("Error stopping AVAudioRecorder: ${e.message}", e)
        } finally {
            recorder = null
            tempFileURL = null
        }

        val data = NSData.dataWithContentsOfURL(fileURL)
        if (data == null) {
            NSFileManager.defaultManager.removeItemAtURL(fileURL, error = null)
            throw AudioRecordingException("Failed to read recorded audio data from file URL.")
        }

        val bytes = ByteArray(data.length.toInt())
        if (bytes.isNotEmpty()) {
            bytes.usePinned { pinned ->
                memcpy(pinned.addressOf(0), data.bytes, data.length)
            }
        }

        // Clean up file
        NSFileManager.defaultManager.removeItemAtURL(fileURL, error = null)
        return bytes
    }

    override fun release() {
        try {
            recorder?.stop()
        } catch (e: Exception) {
            // Ignore stop errors on release
        } finally {
            recorder = null
            tempFileURL?.let { fileURL ->
                NSFileManager.defaultManager.removeItemAtURL(fileURL, error = null)
            }
            tempFileURL = null
        }
    }
}
