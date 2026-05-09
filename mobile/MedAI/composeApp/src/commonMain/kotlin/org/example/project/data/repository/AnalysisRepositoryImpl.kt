package org.example.project.data.repository

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import org.example.project.core.Constants
import org.example.project.domain.model.AnalysisResult
import org.example.project.domain.repository.AnalysisRepository

class AnalysisRepositoryImpl(
    private val httpClient: HttpClient
) : AnalysisRepository {

    override suspend fun analyzeAudio(audioBytes: ByteArray, fileName: String): Result<AnalysisResult> {
        return try {
            val response = httpClient.submitFormWithBinaryData(
                url = "${Constants.BASE_URL}/analysis/audio",
                formData = formData {
                    append("file", audioBytes, Headers.build {
                        append(HttpHeaders.ContentType, "audio/wav")
                        append(HttpHeaders.ContentDisposition, "filename=\"$fileName\"")
                    })
                }
            )

            // Expected 2xx response maps to AnalysisResult
            val result: AnalysisResult = response.body()
            Result.success(result)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
