package kr.co.pincoin.api.app.auth.service

import jakarta.servlet.http.HttpServletRequest
import kr.co.pincoin.api.app.auth.request.SignInRequest
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse
import kr.co.pincoin.api.domain.user.repository.UserRepository
import kr.co.pincoin.api.domain.user.vo.TokenPair
import kr.co.pincoin.api.global.constant.RedisKey
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.JwtAuthenticationException
import kr.co.pincoin.api.global.exception.code.AuthErrorCode
import kr.co.pincoin.api.global.properties.JwtProperties
import kr.co.pincoin.api.global.security.jwt.JwtTokenProvider
import kr.co.pincoin.api.global.utils.IpUtils
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val redisTemplate: RedisTemplate<String, String>
) {
    /**
     * 사용자 로그인 처리 및 토큰 발급
     *
     * @param request 로그인 요청 정보 (이메일, 비밀번호, 자동 로그인 여부)
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (액세스 토큰, 리프레시 토큰)
     * @throws BusinessException 이메일이나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user =
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        val accessToken = jwtTokenProvider.createAccessToken(user)

        // 자동 로그인 요청시에만 리프레시 토큰 발급
        return if (request.rememberMe) {
            // 기존 리프레시 토큰 삭제
            with(redisTemplate) {
                opsForValue().get(user.email)?.let { oldRefreshToken ->
                    delete(oldRefreshToken)
                }
            }

            val refreshToken = jwtTokenProvider.createRefreshToken()
            saveRefreshTokenInfo(refreshToken, user.email, servletRequest)

            TokenPair(
                AccessTokenResponse.of(accessToken, jwtProperties.accessTokenExpiresIn),
                refreshToken
            )
        } else {
            TokenPair(
                AccessTokenResponse.of(accessToken, jwtProperties.accessTokenExpiresIn),
                null
            )
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 토큰 쌍 발급
     *
     * @param refreshToken 기존 리프레시 토큰
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (새로운 액세스 토큰, 새로운 리프레시 토큰)
     * @throws JwtAuthenticationException 토큰이 유효하지 않거나 IP가 불일치하는 경우
     */
    @Transactional
    fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        validateRefreshToken(refreshToken, servletRequest)

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)

            // DB에서 최신 사용자 정보 조회
            val user = userRepository.findUser(
                UserSearchCriteria(email = email, isActive = true)
            ) ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)

            // 최신 사용자 정보로 새 액세스 토큰 생성
            val newAccessToken = jwtTokenProvider.createAccessToken(user)
            val newRefreshToken = jwtTokenProvider.createRefreshToken()

            delete(refreshToken)
            saveRefreshTokenInfo(newRefreshToken, email, servletRequest)

            return TokenPair(
                accessToken = AccessTokenResponse.of(
                    newAccessToken,
                    jwtProperties.accessTokenExpiresIn
                ),
                refreshToken = newRefreshToken
            )
        }
    }

    /**
     * 로그아웃 처리 Redis에서 리프레시 토큰과 관련 정보 삭제
     */
    fun logout(refreshToken: String) {
        with(redisTemplate) {
            val email = opsForHash<String, String>().get(refreshToken, RedisKey.EMAIL) ?: return

            delete(refreshToken)
            delete(email)
        }
    }

    /**
     * Redis에 리프레시 토큰 관련 정보 저장
     * - 리프레시 토큰을 키로 하는 해시: 이메일, IP 주소 저장
     * - 이메일을 키로 하는 문자열: 리프레시 토큰 저장
     */
    private fun saveRefreshTokenInfo(
        refreshToken: String,
        email: String,
        request: HttpServletRequest
    ) {
        val clientIp = IpUtils.getClientIp(request)

        with(redisTemplate) {
            // 리프레시 토큰 해시에 이메일과 IP 주소 저장
            opsForHash<String, String>()
                .putAll(
                    refreshToken,
                    mapOf(RedisKey.EMAIL to email, RedisKey.IP_ADDRESS to clientIp)
                )

            // 리프레시 토큰 만료 시간 설정
            expire(refreshToken, jwtProperties.refreshTokenExpiresIn, TimeUnit.SECONDS)

            // 이메일로 리프레시 토큰 조회를 위한 키-값 저장
            opsForValue().set(
                email,
                refreshToken,
                jwtProperties.refreshTokenExpiresIn,
                TimeUnit.SECONDS
            )
        }
    }

    /**
     * 리프레시 토큰 유효성 검증
     * - UUID 형식 검증
     * - Redis에 저장된 토큰인지 확인
     * - 요청 IP와 저장된 IP 일치 여부 확인
     */
    private fun validateRefreshToken(refreshToken: String, request: HttpServletRequest) {
        if (!refreshToken.matches(UUID_PATTERN.toRegex())) {
            throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        with(redisTemplate.opsForHash<String, String>()) {
            val email = get(refreshToken, RedisKey.EMAIL)
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)

            val storedIp = get(refreshToken, RedisKey.IP_ADDRESS)
            val currentIp = IpUtils.getClientIp(request)
            if (storedIp == null || storedIp != currentIp) {
                throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
            }
        }
    }

    companion object {
        private const val UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    }
}