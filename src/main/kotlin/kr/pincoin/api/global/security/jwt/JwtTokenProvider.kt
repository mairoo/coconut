package kr.pincoin.api.global.security.jwt

import kr.pincoin.api.domain.user.error.AuthErrorCode
import kr.pincoin.api.global.exception.JwtAuthenticationException
import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.io.DecodingException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.properties.JwtProperties
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val log = KotlinLogging.logger {}

    // @PostConstruct 스프링/자바 방식 대신에 lazy delegate 활용
    // 순수 코틀린 기능 활용한 필요한 시점에 초기화 (thread-safe 지연 초기화)
    private val key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtProperties.secret))
    }

    private val headers by lazy {
        mapOf(
            "typ" to JWT_TYPE,
            "alg" to JWT_ALGORITHM
        )
    }

    fun createAccessToken(user: User): String {
        val now = Date()
        val validity = Date(now.time + jwtProperties.accessTokenExpiresIn * 1000L)

        return Jwts.builder()
            .header()
            .add(headers)
            .and()
            .subject(user.email)
            .claim("username", user.username)
            .issuedAt(now)
            .expiration(validity)
            .signWith(key, Jwts.SIG.HS512)
            .compact()
    }

    fun createRefreshToken(): String = UUID.randomUUID().toString()

    fun validateToken(jws: String): String? = runCatching {
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(jws)
            .payload
            .subject
    }.getOrElse { exception ->
        when (exception) {
            is ExpiredJwtException -> {
                log.warn { "만료된 토큰: ${exception.message}" }
                throw JwtAuthenticationException(AuthErrorCode.EXPIRED_TOKEN)
            }

            is SignatureException,
            is DecodingException,
            is UnsupportedJwtException,
            is MalformedJwtException,
            is IllegalArgumentException -> {
                log.warn { "유효하지 않은 토큰: ${exception.message}" }
                throw JwtAuthenticationException(AuthErrorCode.INVALID_TOKEN)
            }

            else -> {
                log.error(exception) { "예기치 못한 오류" }
                throw JwtAuthenticationException(AuthErrorCode.UNEXPECTED)
            }
        }
    }

    companion object {
        const val JWT_TYPE = "JWT"
        const val JWT_ALGORITHM = "HS512"
    }
}