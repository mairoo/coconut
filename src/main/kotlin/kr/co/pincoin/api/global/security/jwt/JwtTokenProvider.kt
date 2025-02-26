package kr.co.pincoin.api.global.security.jwt

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.global.exception.JwtAuthenticationException
import kr.co.pincoin.api.global.exception.code.AuthErrorCode
import kr.co.pincoin.api.global.properties.JwtProperties
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

        val authorities = mutableListOf("ROLE_USER")
        if (user.isSuperuser) {
            authorities.add("ROLE_ADMIN")
        }
        if (user.isStaff) {
            authorities.add("ROLE_STAFF")
        }

        return Jwts.builder()
            .header()
            .add(headers)
            .and()
            .subject(user.email)
            .claim("authorities", authorities)
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

            is io.jsonwebtoken.security.SignatureException,
            is io.jsonwebtoken.io.DecodingException,
            is io.jsonwebtoken.UnsupportedJwtException,
            is io.jsonwebtoken.MalformedJwtException,
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