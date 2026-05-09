package org.example.project.presentation.analysis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.model.AnalysisResult
import org.example.project.domain.services.AudioRecorder
import org.example.project.domain.usecase.AnalyzeAudioUseCase

enum class AnalysisState {
    IDLE,
    RECORDING,
    UPLOADING,
    ANALYZING,
    SUCCESS,
    ERROR
}

class AnalysisViewModel(
    private val audioRecorder: AudioRecorder,
    private val analyzeAudioUseCase: AnalyzeAudioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalysisState.IDLE)
    val uiState: StateFlow<AnalysisState> = _uiState.asStateFlow()

    private val _result = MutableStateFlow<AnalysisResult?>(null)
    val result: StateFlow<AnalysisResult?> = _result.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun toggleRecording() {
        if (_uiState.value == AnalysisState.RECORDING) {
            stopRecordingAndAnalyze()
        } else {
            startRecording()
        }
    }

    private fun startRecording() {
        try {
            audioRecorder.startRecording()
            _uiState.value = AnalysisState.RECORDING
        } catch (e: Exception) {
            _errorMessage.value = "Failed to start recording: ${e.message}"
            _uiState.value = AnalysisState.ERROR
        }
    }

    private fun stopRecordingAndAnalyze() {
        _uiState.value = AnalysisState.UPLOADING
        val audioBytes = audioRecorder.stopRecording()

        if (audioBytes == null || audioBytes.isEmpty()) {
            _errorMessage.value = "Recorded audio is empty."
            _uiState.value = AnalysisState.ERROR
            return
        }

        viewModelScope.launch {
            _uiState.value = AnalysisState.ANALYZING

            analyzeAudioUseCase(audioBytes, "recording.wav").fold(
                onSuccess = { analysisResult ->
                    _result.value = analysisResult
                    _uiState.value = AnalysisState.SUCCESS
                },
                onFailure = { error ->
                    _errorMessage.value = error.message ?: "An unknown error occurred during analysis."
                    _uiState.value = AnalysisState.ERROR
                }
            )
        }
    }

    fun reset() {
        _uiState.value = AnalysisState.IDLE
        _result.value = null
        _errorMessage.value = null
    }
}
