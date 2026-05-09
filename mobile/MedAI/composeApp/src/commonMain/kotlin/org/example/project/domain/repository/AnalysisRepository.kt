package org.example.project.domain.repository

import org.example.project.domain.model.AnalysisResult

interface AnalysisRepository {
    suspend fun analyzeAudio(audioBytes: ByteArray, fileName: String): Result<AnalysisResult>
}
