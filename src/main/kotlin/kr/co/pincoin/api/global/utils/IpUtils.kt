package kr.co.pincoin.api.global.utils

import jakarta.servlet.http.HttpServletRequest

object IpUtils {
    private val IP_HEADERS = arrayOf(
        "CF-Connecting-IP", // Cloudflare
        "X-Real-IP", // Nginx
        "X-Forwarded-For",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_CLIENT_IP"
    )

    fun getClientIp(request: HttpServletRequest): String {
        return IP_HEADERS
            .mapNotNull { header -> request.getHeader(header) }
            .firstOrNull { ip ->
                ip.isNotEmpty() && !ip.equals("unknown", ignoreCase = true)
            }
            ?.let { ip ->
                if (IP_HEADERS[2] == "X-Forwarded-For") ip.split(",")[0]
                else ip
            } ?: request.remoteAddr
    }
}