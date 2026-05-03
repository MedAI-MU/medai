package org.example.project.domain.services

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

class AndroidAudioRecorder : AudioRecorder {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false
    private var recordingJob: Job? = null
    private val outputStream = ByteArrayOutputStream()

    @SuppressLint("MissingPermission")
    override fun startRecording() {
        val sampleRate = 16000 // 16kHz for Whisper
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )

        audioRecord?.startRecording()
        isRecording = true
        outputStream.reset()

        recordingJob = CoroutineScope(Dispatchers.IO).launch {
            val audioBuffer = ByteArray(bufferSize)
            while (isRecording) {
                val bytesRead = audioRecord?.read(audioBuffer, 0, audioBuffer.size) ?: 0
                if (bytesRead > 0) {
                    outputStream.write(audioBuffer, 0, bytesRead)
                }
            }
        }
    }

    override fun stopRecording(): ByteArray? {
        isRecording = false
        recordingJob?.cancel()
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        // Output raw PCM (or we could add a WAV header here if strictly needed by backend)
        val pcmData = outputStream.toByteArray()
        return addWavHeader(pcmData, 16000, 1, 16)
    }

    override fun isRecording(): Boolean = isRecording

    private fun addWavHeader(pcmData: ByteArray, sampleRate: Int, channels: Int, bitDepth: Int): ByteArray {
        val byteRate = sampleRate * channels * (bitDepth / 8)
        val header = ByteArray(44)

        header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
        val totalDataLen = pcmData.size + 36
        header[4] = (totalDataLen and 0xff).toByte()
        header[5] = (totalDataLen shr 8 and 0xff).toByte()
        header[6] = (totalDataLen shr 16 and 0xff).toByte()
        header[7] = (totalDataLen shr 24 and 0xff).toByte()

        header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
        header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()

        header[16] = 16; header[17] = 0; header[18] = 0; header[19] = 0
        header[20] = 1; header[21] = 0
        header[22] = channels.toByte(); header[23] = 0

        header[24] = (sampleRate and 0xff).toByte()
        header[25] = (sampleRate shr 8 and 0xff).toByte()
        header[26] = (sampleRate shr 16 and 0xff).toByte()
        header[27] = (sampleRate shr 24 and 0xff).toByte()

        header[28] = (byteRate and 0xff).toByte()
        header[29] = (byteRate shr 8 and 0xff).toByte()
        header[30] = (byteRate shr 16 and 0xff).toByte()
        header[31] = (byteRate shr 24 and 0xff).toByte()

        header[32] = (channels * (bitDepth / 8)).toByte(); header[33] = 0
        header[34] = bitDepth.toByte(); header[35] = 0

        header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()

        val pcmDataLen = pcmData.size
        header[40] = (pcmDataLen and 0xff).toByte()
        header[41] = (pcmDataLen shr 8 and 0xff).toByte()
        header[42] = (pcmDataLen shr 16 and 0xff).toByte()
        header[43] = (pcmDataLen shr 24 and 0xff).toByte()

        val out = ByteArrayOutputStream()
        out.write(header)
        out.write(pcmData)
        return out.toByteArray()
    }
}
