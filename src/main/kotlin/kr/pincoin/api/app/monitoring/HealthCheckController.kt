package kr.pincoin.api.app.monitoring

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.response.RequestInfoResponse
import kr.pincoin.api.global.properties.JwtProperties
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.utils.IpUtils
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckController(
    private val jwtProperties: JwtProperties,
) {
    @GetMapping("/health", produces = ["text/plain"])
    fun health(): String {
        return "OK"
    }

    @GetMapping("/echo")
    fun echo(
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<RequestInfoResponse>> {
        // 헤더 정보 추출
        val headerNames = servletRequest.headerNames.toList()
        val headers = headerNames.associateWith { servletRequest.getHeader(it) }

        // 쿠키 정보 추출
        val cookies = servletRequest.cookies?.associate {
            it.name to it.value
        } ?: emptyMap()

        // 클라이언트 정보
        val clientInfo = RequestInfoResponse.ClientInfo(
            remoteAddr = servletRequest.remoteAddr,
            remoteHost = servletRequest.remoteHost,
            remotePort = servletRequest.remotePort,
            resolvedClientIp = IpUtils.getClientIp(servletRequest),
            userAgent = servletRequest.getHeader("User-Agent").orEmpty(),
            acceptLanguage = servletRequest.getHeader("Accept-Language").orEmpty(),
        )

        // 서버 정보
        val requestDomain = servletRequest.serverName
        val calculatedCookieDomain = jwtProperties.findCookieDomain(requestDomain)

        val serverInfo = RequestInfoResponse.ServerInfo(
            serverName = servletRequest.serverName,
            serverPort = servletRequest.serverPort,
            scheme = servletRequest.scheme,
            requestURI = servletRequest.requestURI,
            requestURL = servletRequest.requestURL.toString(),
            contextPath = servletRequest.contextPath,
            servletPath = servletRequest.servletPath,
            queryString = servletRequest.queryString,
            calculatedCookieDomain = calculatedCookieDomain,
        )

        // JWT 정보
        val jwtInfo = RequestInfoResponse.JwtInfo(
            cookieDomains = jwtProperties.cookieDomains,
        )

        val responseData = RequestInfoResponse(
            headers = headers,
            serverInfo = serverInfo,
            clientInfo = clientInfo,
            cookieInfo = cookies,
            jwtInfo = jwtInfo,
        )

        return ResponseEntity.ok()
            .body(ApiResponse.of(responseData))
    }
}