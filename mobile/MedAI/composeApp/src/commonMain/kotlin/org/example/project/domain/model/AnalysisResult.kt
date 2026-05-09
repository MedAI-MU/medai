package org.example.project.domain.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class AnalysisResult(
    val transcription: String,
    val clinical_analysis: JsonElement,
    val performance: PerformanceMetrics
)

@Serializable
data class PerformanceMetrics(
    val stt_latency_sec: Double,
    val nlu_latency_sec: Double,
    val total_pipeline_sec: Double
)
