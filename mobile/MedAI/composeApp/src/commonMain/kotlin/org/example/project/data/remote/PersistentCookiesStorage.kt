package org.example.project.data.remote

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.example.project.domain.repository.auth.UserSessionManager

class PersistentCookiesStorage(
    private val sessionManager: UserSessionManager
) : CookiesStorage {
    private val mutex = Mutex()
    private val memCache = mutableListOf<Cookie>()
    private var isLoaded = false

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        mutex.withLock {
            ensureLoaded()
            memCache.removeAll { it.name == cookie.name && it.matches(requestUrl) }
            memCache.add(cookie.fillDefaults(requestUrl))
            saveToStorage()
        }
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        mutex.withLock {
            ensureLoaded()
            return memCache.filter { it.matches(requestUrl) }
        }
    }

    override fun close() {}

    private suspend fun ensureLoaded() {
        if (!isLoaded) {
            val savedCookies = sessionManager.getCookies()
            memCache.addAll(savedCookies.mapNotNull { parseCookie(it) })
            isLoaded = true
        }
    }

    private suspend fun saveToStorage() {
        // Serialize cookies to simple string format: "name=value; domain=...; path=..."
        // Or deeper serialization if needed. For Ktor, toString() often gives Set-Cookie format.
        // But we need to reconstruct them later.
        val cookieStrings = memCache.map { renderCookie(it) }.toSet()
        sessionManager.saveCookies(cookieStrings)
    }

    private fun renderCookie(cookie: Cookie): String {
        // A simple serialization strategy.
        // Format: Name=Value;Domain=...;Path=...;Expires=...
        // We can use Ktor's own rendering or build a custom string.
        // Ideally we store all properties.
        return "${cookie.name}=${cookie.value};Domain=${cookie.domain ?: ""};Path=${cookie.path ?: ""};Secure=${cookie.secure};HttpOnly=${cookie.httpOnly}"
    }

    private fun parseCookie(cookieString: String): Cookie? {
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

            return Cookie(name, value, domain = domain?.takeIf { it.isNotEmpty() }, path = path?.takeIf { it.isNotEmpty() }, secure = secure, httpOnly = httpOnly)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun Cookie.matches(requestUrl: Url): Boolean {
        val domain = this.domain
        val path = this.path

        if (domain != null && !requestUrl.host.endsWith(domain)) return false
        if (path != null && !requestUrl.encodedPath.startsWith(path)) return false

        return true
    }

    private fun Cookie.fillDefaults(requestUrl: Url): Cookie {
        var result = this
        if (result.path?.startsWith("/") != true) {
            result = result.copy(path = requestUrl.encodedPath)
        }
        if (result.domain.isNullOrBlank()) {
            result = result.copy(domain = requestUrl.host)
        }
        return result
    }
}
