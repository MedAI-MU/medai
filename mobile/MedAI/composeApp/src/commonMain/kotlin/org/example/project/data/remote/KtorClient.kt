package org.example.project.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.project.domain.repository.auth.UserSessionManager
import io.ktor.http.HttpStatusCode
import io.ktor.http.encodedPath
import io.ktor.http.takeFrom
import io.ktor.http.Url
import org.example.project.AppConfig

class KtorClientFactory(
    private val sessionManager: UserSessionManager
) {
    private val BASE_URL = AppConfig.BASE_URL
 //   private val BASE_URL = "http://10.0.2.2:8000/api/"
    private val backendHost = try {
        Url(AppConfig.BASE_URL).host
    } catch (e: Exception) {
        ""
    }

    private val refreshClient by lazy {
        HttpClient {
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
                        println("Network-Refresh: $message")
                    }
                }
                level = LogLevel.ALL
            }
            defaultRequest {
                url.takeFrom(AppConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }.apply {
            plugin(HttpSend).intercept { request ->
                val requestHost = request.url.host
                val isBackendRequest = requestHost.isEmpty() || requestHost == backendHost || requestHost == "10.0.2.2" || requestHost == "localhost"
                if (isBackendRequest) {
                    val originalPath = request.url.encodedPath
                    if (!originalPath.startsWith("/api/")) {
                        val newPath = "/api" + if (originalPath.startsWith("/")) originalPath else "/$originalPath"
                        request.url.encodedPath = newPath
                    }
                }
                execute(request)
            }
        }
    }

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

            install(Auth) {
                bearer {
                    loadTokens {
                        val accessToken = sessionManager.getUserToken()
                        val cookies = sessionManager.getCookies()
                        val refreshToken = cookies.mapNotNull { parseCookie(it) }
                            .find { it.name == "Refresh" }?.value

                        println("Auth-Bearer: loadTokens called. AccessToken exists: ${!accessToken.isNullOrBlank()}, RefreshToken exists: ${!refreshToken.isNullOrBlank()}")
                        if (accessToken != null) {
                            BearerTokens(accessToken, refreshToken ?: "")
                        } else {
                            null
                        }
                    }

                    refreshTokens {
                        try {
                            println("Auth-Bearer: Token expired or unauthorized, triggering silent token refresh...")
                            val refreshResponse = refreshClient.post("auth/refresh-token")

                            if (refreshResponse.status == HttpStatusCode.OK) {
                                println("Auth-Bearer: Silent token refresh succeeded!")
                                val savedCookies = sessionManager.getCookies()
                                val newAccessToken = savedCookies.mapNotNull { parseCookie(it) }
                                    .find { it.name == "Authentication" }?.value

                                val newRefreshToken = savedCookies.mapNotNull { parseCookie(it) }
                                    .find { it.name == "Refresh" }?.value

                                if (newAccessToken != null) {
                                    sessionManager.updateUserToken(newAccessToken)
                                    BearerTokens(newAccessToken, newRefreshToken ?: "")
                                } else {
                                    println("Auth-Bearer: Refresh succeeded but new access token not found in cookies.")
                                    sessionManager.clearSession()
                                    null
                                }
                            } else {
                                println("Auth-Bearer: Silent token refresh request failed with status: ${refreshResponse.status}. Clearing session.")
                                sessionManager.clearSession()
                                null
                            }
                        } catch (e: Exception) {
                            println("Auth-Bearer: Silent token refresh failed with exception: ${e.message}. Clearing session.")
                            sessionManager.clearSession()
                            null
                        }
                    }
                }
            }

            defaultRequest {
                url.takeFrom(AppConfig.BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }.apply {
             plugin(HttpSend).intercept { request ->
                 val requestHost = request.url.host
                 val isBackendRequest = requestHost.isEmpty() || requestHost == backendHost || requestHost == "10.0.2.2" || requestHost == "localhost"

                 if (isBackendRequest) {
                     val originalPath = request.url.encodedPath
                     if (!originalPath.startsWith("/api/")) {
                         val newPath = "/api" + if (originalPath.startsWith("/")) originalPath else "/$originalPath"
                         request.url.encodedPath = newPath
                     }
                 } else {
                     // For external hosts (e.g. Azure Blob storage), do not leak backend Auth bearer token
                     request.headers.remove("Authorization")
                 }

                 val originalCall = execute(request)

                 // Handle 403 Forbidden if encountered and user is logged in
                 // Under typical circumstances, a 403 signifies permission issue rather than expiration.
                 // But if mapped to 401, it forces the Auth plugin to challenge and refresh.
                 if (isBackendRequest && originalCall.response.status == HttpStatusCode.Forbidden) {
                     val token = sessionManager.getUserToken()
                     if (!token.isNullOrBlank()) {
                         println("Auth-Bearer: 403 Forbidden detected and user has active session. Treating as unauthorized challenge.")
                     }
                 }

                 originalCall
             }
        }
    }
}

private fun parseCookie(cookieString: String): io.ktor.http.Cookie? {
    try {
        val parts = cookieString.split(";")
        val nameValue = parts[0].split("=")
        if (nameValue.size < 2) return null

        val name = nameValue[0]
        val value = nameValue[1]

        var domain: String? = null
        var path: String? = null
        var secure = false
        var httpOnly = false

        for (i in 1 until parts.size) {
            val part = parts[i].trim()
            when {
                part.startsWith("Domain=") -> domain = part.substringAfter("Domain=")
                part.startsWith("Path=") -> path = part.substringAfter("Path=")
                part == "Secure=true" -> secure = true
                part == "HttpOnly=true" -> httpOnly = true
            }
        }

        return io.ktor.http.Cookie(name, value, domain = domain?.takeIf { it.isNotEmpty() }, path = path?.takeIf { it.isNotEmpty() }, secure = secure, httpOnly = httpOnly)
    } catch (e: Exception) {
        return null
    }
}
