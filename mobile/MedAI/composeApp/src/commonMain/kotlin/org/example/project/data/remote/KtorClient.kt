package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.post
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.domain.repository.UserSessionManager
import io.ktor.client.plugins.HttpSend
import io.ktor.client.request.url
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLProtocol
import io.ktor.http.encodedPath
import io.ktor.http.path
import io.ktor.http.takeFrom

class KtorClientFactory(
    private val sessionManager: UserSessionManager
) {
    private val BASE_URL = "http://10.0.2.2:8000/api/"

    fun create(): HttpClient {
        return HttpClient {
            expectSuccess = true // Throw exception on non-2xx

            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(HttpCookies) {
                storage = PersistentCookiesStorage(sessionManager)
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        println("Network: $message")
                    }
                }
                level = LogLevel.ALL
            }

            defaultRequest {
                url.protocol = URLProtocol.HTTP
                url.host = "10.0.2.2"
                url.port = 8000
                contentType(ContentType.Application.Json)
            }
        }.apply {
             plugin(HttpSend).intercept { request ->

                 val originalPath = request.url.encodedPath
                 if (!originalPath.startsWith("/api/")) {
                     val newPath = "/api" + if (originalPath.startsWith("/")) originalPath else "/$originalPath"
                     request.url.encodedPath = newPath
                 }

                // Inject Token
                try {
                    val token = sessionManager.getUserToken()
                    if (!token.isNullOrBlank()) {
                        request.headers.append("Authorization", "Bearer $token")
                    }
                } catch (e: Exception) {
                    println("Auth: Failed to get token: ${e.message}")
                }

                val originalCall = execute(request)
                if (originalCall.response.status == HttpStatusCode.Unauthorized) {
                     println("Auth: 401 Detected. Attempting refresh...")
                     // Pause and Refresh
                     try {
                         // We create a separate client or raw request to avoid infinite loop if refresh fails
                         // But here we can use the same client if we are careful, or just manually construct the request
                         // To be safe, let's use a side-channel or just retry once?
                         // Ktor's Auth plugin handles this better, but we are doing cookies.

                         // Manually call refresh endpoint.
                         // The Cookies plugin will automatically send the "Refresh" cookie if it exists in storage!
                         val refreshResponse = execute(HttpRequestBuilder().apply {
                             url(BASE_URL + "auth/refresh-token")
                             method = io.ktor.http.HttpMethod.Post
                         })

                         if (refreshResponse.response.status == HttpStatusCode.OK) {
                             println("Auth: Refresh Success! Retrying original request...")
                             // Cookies are auto-updated in storage by the plugin from the refresh response
                             execute(request)
                         } else {
                             println("Auth: Refresh Failed. Logout.")
                             sessionManager.clearSession()
                             originalCall
                         }
                     } catch (e: Exception) {
                         println("Auth: Refresh Error: ${e.message}")
                         sessionManager.clearSession()
                         originalCall
                     }
                } else {
                    originalCall
                }
            }
        }
    }
}
