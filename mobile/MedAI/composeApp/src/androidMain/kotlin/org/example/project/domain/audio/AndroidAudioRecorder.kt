package org.example.project.domain.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import java.io.File
import java.io.FileInputStream

class AndroidAudioRecorder(
    private val context: Context
) : AudioRecorder {

    private val tag = "AndroidAudioRecorder"
    private var mediaRecorder: MediaRecorder? = null
    private var tempFile: File? = null

    override fun startRecording() {
        try {
            tempFile = File(context.cacheDir, "temp_recording.m4a").apply {
                if (exists()) delete()
            }

            @Suppress("DEPRECATION")
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                MediaRecorder()
            }.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(tempFile!!.absolutePath)
                prepare()
                start()
            }
            Log.d(tag, "Recording started at: ${tempFile?.absolutePath}")
        } catch (e: Exception) {
            Log.e(tag, "Failed to start recording: ${e.message}", e)
            mediaRecorder = null
            tempFile?.delete()
            tempFile = null
            throw AudioRecordingException("Failed to start audio recording: ${e.message}", e)
        }
    }

    override fun stopRecording(): ByteArray {
        var audioBytes = ByteArray(0)
        try {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping recorder: ${e.message}")
            throw AudioRecordingException("Failed to stop audio recording properly: ${e.message}", e)
        } finally {
            mediaRecorder = null
        }

        val file = tempFile
        if (file != null && file.exists() && file.length() > 0) {
            try {
                FileInputStream(file).use { fis ->
                    audioBytes = fis.readBytes()
                }
                file.delete()
            } catch (e: Exception) {
                Log.e(tag, "Error reading recorded file: ${e.message}")
                throw AudioRecordingException("Failed to read recorded audio file: ${e.message}", e)
            }
        } else {
            throw AudioRecordingException("No audio recording file was captured.")
        }

        tempFile = null
        return audioBytes
    }

    override fun release() {
        try {
            mediaRecorder?.release()
        } catch (e: Exception) {
            Log.e(tag, "Error releasing recorder: ${e.message}")
        } finally {
            mediaRecorder = null
            tempFile?.delete()
            tempFile = null
        }
    }
}
