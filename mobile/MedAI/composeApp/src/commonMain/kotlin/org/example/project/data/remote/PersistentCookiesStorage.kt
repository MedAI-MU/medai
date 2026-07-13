package org.example.project.data.remote

import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Cookie
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import org.example.project.domain.repository.auth.UserSessionManager

class PersistentCookiesStorage(
    private val sessionManager: UserSessionManager
) : CookiesStorage {

    override suspend fun addCookie(requestUrl: Url, cookie: Cookie) {
        val savedCookies = sessionManager.getCookies()
        val cookiesList = savedCookies.mapNotNull { parseCookie(it) }.toMutableList()

        // Remove matching old cookies
        cookiesList.removeAll { it.name == cookie.name && it.matches(requestUrl) }

        // Add new cookie
        cookiesList.add(cookie.fillDefaults(requestUrl))

        // Save back to storage
        val cookieStrings = cookiesList.map { renderCookie(it) }.toSet()
        sessionManager.saveCookies(cookieStrings)
    }

    override suspend fun get(requestUrl: Url): List<Cookie> {
        val savedCookies = sessionManager.getCookies()
        val cookiesList = savedCookies.mapNotNull { parseCookie(it) }
        return cookiesList.filter { it.matches(requestUrl) }
    }

    override fun close() {}

    private fun renderCookie(cookie: Cookie): String {
        return "${cookie.name}=${cookie.value};Domain=${cookie.domain ?: ""};Path=${cookie.path ?: ""};Secure=${cookie.secure};HttpOnly=${cookie.httpOnly};Expires=${cookie.expires?.timestamp ?: ""}"
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
            var expires: GMTDate? = null

            for (i in 1 until parts.size) {
                val part = parts[i].trim()
                when {
                    part.startsWith("Domain=") -> domain = part.substringAfter("Domain=")
                    part.startsWith("Path=") -> path = part.substringAfter("Path=")
                    part == "Secure=true" -> secure = true
                    part == "HttpOnly=true" -> httpOnly = true
                    part.startsWith("Expires=") -> {
                        val ts = part.substringAfter("Expires=").toLongOrNull()
                        if (ts != null) {
                            expires = GMTDate(ts)
                        }
                    }
                }
            }

            return Cookie(
                name = name,
                value = value,
                expires = expires,
                domain = domain?.takeIf { it.isNotEmpty() },
                path = path?.takeIf { it.isNotEmpty() },
                secure = secure,
                httpOnly = httpOnly
            )
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
        if (result.path.isNullOrEmpty()) {
            result = result.copy(path = "/")
        }
        if (result.domain.isNullOrBlank()) {
            result = result.copy(domain = requestUrl.host)
        }
        return result
    }
}
