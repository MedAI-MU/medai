package org.example.project.domain.usecase

import org.example.project.domain.model.AnalysisResult
import org.example.project.domain.repository.AnalysisRepository

class AnalyzeAudioUseCase(private val repository: AnalysisRepository) {
    suspend operator fun invoke(audioBytes: ByteArray, fileName: String): Result<AnalysisResult> {
        return repository.analyzeAudio(audioBytes, fileName)
    }
}
