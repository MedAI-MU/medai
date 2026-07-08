package org.example.project.presentation.doctor.services.xray

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.example.project.domain.ai.XRayClassifier

class XRayAnalysisViewModel(
    private val classifier: XRayClassifier
) : ScreenModel {

    private val _uiState = MutableStateFlow<XRayAnalysisUiState>(XRayAnalysisUiState.Idle)
    val uiState: StateFlow<XRayAnalysisUiState> = _uiState

    fun analyzeImage(imageBytes: ByteArray) {
        screenModelScope.launch {
            try {
                // 1. Image Loaded
                _uiState.value = XRayAnalysisUiState.Loading("Reading X-Ray Plate...")
                delay(600)

                // 2. Preprocessing
                _uiState.value = XRayAnalysisUiState.Loading("Applying samplewise mean/std normalization...")
                delay(600)

                // 3. Neural Net inference
                _uiState.value = XRayAnalysisUiState.Loading("Running neural network on 14 pathological targets...")

                // Perform TFLite or Mock prediction
                val results = classifier.classifyImage(imageBytes)

                // 4. Formatting output
                _uiState.value = XRayAnalysisUiState.Loading("Compiling clinical results...")
                delay(500)

                // Sort by probability descending (highest risk first)
                val sortedResults = results.toList().sortedByDescending { it.second }
                _uiState.value = XRayAnalysisUiState.Success(sortedResults)
            } catch (e: Exception) {
                _uiState.value = XRayAnalysisUiState.Error("Analysis failed: ${e.message ?: "Inference error."}")
            }
        }
    }

    fun resetState() {
        _uiState.value = XRayAnalysisUiState.Idle
    }
}

sealed class XRayAnalysisUiState {
    object Idle : XRayAnalysisUiState()
    data class Loading(val message: String) : XRayAnalysisUiState()
    data class Success(val predictions: List<Pair<String, Float>>) : XRayAnalysisUiState()
    data class Error(val message: String) : XRayAnalysisUiState()
}
