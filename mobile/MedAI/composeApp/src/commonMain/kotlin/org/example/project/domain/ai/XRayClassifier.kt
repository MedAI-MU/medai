package org.example.project.domain.ai

interface XRayClassifier {
    /**
     * Takes image bytes, performs TFLite on-device inference,
     * and returns a map of the 14 pathologies to their risk probability (0.0 to 1.0).
     */
    suspend fun classifyImage(imageBytes: ByteArray): Map<String, Float>
}
