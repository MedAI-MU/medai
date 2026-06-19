package org.example.project.domain.ai

import kotlinx.coroutines.delay
import kotlin.math.abs

class IosXRayClassifier : XRayClassifier {
    private val labels = listOf(
        "Cardiomegaly",
        "Emphysema",
        "Effusion",
        "Hernia",
        "Infiltration",
        "Mass",
        "Nodule",
        "Atelectasis",
        "Pneumothorax",
        "Pleural_Thickening",
        "Pneumonia",
        "Fibrosis",
        "Edema",
        "Consolidation"
    )

    override suspend fun classifyImage(imageBytes: ByteArray): Map<String, Float> {
        delay(1200) // Simulate processing delay
        val byteSum = imageBytes.fold(0L) { acc, byte -> acc + abs(byte.toInt()) }

        // Simple stable LCG for deterministic results per image in Kotlin Common
        var seed = byteSum
        fun nextFloat(): Float {
            seed = (seed * 1103515245 + 12345) and 0x7fffffffL
            return seed.toFloat() / 0x7fffffffL.toFloat()
        }

        return labels.associateWith { label ->
            val randVal = nextFloat()
            when (label) {
                "Cardiomegaly" -> if (randVal > 0.5) 0.74f else 0.03f
                "Effusion" -> if (randVal > 0.6) 0.60f else 0.01f
                "Infiltration" -> 0.10f + randVal * 0.20f
                "Nodule" -> if (randVal > 0.8) 0.52f else 0.04f
                "Pneumonia" -> if (randVal > 0.9) 0.81f else 0.01f
                else -> randVal * 0.06f
            }
        }
    }
}
