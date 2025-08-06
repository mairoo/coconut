package kr.pincoin.api.app.oauth2.service

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.oauth2.request.OAuth2CallbackRequest
import kr.pincoin.api.app.oauth2.response.OAuth2LoginUrlResponse
import kr.pincoin.api.app.oauth2.response.OAuth2TokenResponse
import kr.pincoin.api.domain.auth.properties.AuthProperties
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTokenService
import kr.pincoin.api.external.auth.keycloak.service.KeycloakUserService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import kr.pincoin.api.global.utils.OAuth2Utils
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class OAuth2Service(
    private val keycloakProperties: KeycloakProperties,
    private val keycloakTokenService: KeycloakTokenService,
    private val keycloakUserService: KeycloakUserService,
    private val socialMigrationService: SocialMigrationService,
    private val authProperties: AuthProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * OAuth2 로그인 URL 생성
     */
    fun generateLoginUrl(
        redirectUri: String,
        httpServletRequest: HttpServletRequest,
    ): OAuth2LoginUrlResponse {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

        // redirect_uri 보안 검증
        OAuth2Utils.validateRedirectUri(
            redirectUri = redirectUri,
            allowedUris = keycloakProperties.allowedRedirectUris,
            clientInfo = clientInfo,
        )

        // CSRF 방어용 state 생성
        val state = OAuth2Utils.generateSecureState()

        // state를 Redis에 저장 (CSRF 방어)
        saveStateToRedis(state, clientInfo)

        // Keycloak authorization URL 구성
        val baseUrl = "${keycloakProperties.publicUrl}/realms/${keycloakProperties.realm}/protocol/openid-connect/auth"
        val loginUrl = OAuth2Utils.buildAuthorizationUrl(
            baseUrl = baseUrl,
            clientId = keycloakProperties.clientId,
            redirectUri = redirectUri,
            state = state,
        )

        return OAuth2LoginUrlResponse.of(loginUrl, state)
    }

    /**
     * Authorization Code를 Access Token으로 교환 및 소셜 마이그레이션 처리
     */
    fun exchangeCodeForToken(
        request: OAuth2CallbackRequest,
        httpServletRequest: HttpServletRequest,
    ): OAuth2TokenResponse {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

        // redirect_uri 재검증
        OAuth2Utils.validateRedirectUri(
            redirectUri = request.redirectUri,
            allowedUris = keycloakProperties.allowedRedirectUris,
            clientInfo = clientInfo,
        )

        // state 검증 및 소비 (일회성 보장)
        validateAndConsumeState(request.state, clientInfo)

        // Keycloak에서 토큰 교환
        val tokenResponse = runBlocking {
            when (val result = keycloakTokenService.exchangeAuthorizationCode(
                code = request.code,
                redirectUri = request.redirectUri,
            )) {
                is KeycloakResponse.Success -> result.data
                is KeycloakResponse.Error -> {
                    logger.error { "토큰 교환 실패: ${result.errorCode} - ${result.errorMessage}" }
                    throw when (result.errorCode) {
                        "invalid_grant" -> BusinessException(UserErrorCode.INVALID_AUTHORIZATION_CODE)
                        "invalid_client" -> BusinessException(UserErrorCode.INVALID_CLIENT_CREDENTIALS)
                        "TIMEOUT" -> BusinessException(UserErrorCode.TOKEN_EXCHANGE_FAILED)
                        else -> BusinessException(UserErrorCode.TOKEN_EXCHANGE_FAILED)
                    }
                }
            }
        }

        // Access Token으로 Keycloak 사용자 정보 조회
        val keycloakUserInfo = runBlocking {
            when (val result = keycloakUserService.getUserInfo(tokenResponse.accessToken)) {
                is KeycloakResponse.Success -> result.data
                is KeycloakResponse.Error -> {
                    logger.error {
                        "Keycloak 사용자 정보 조회 실패: ${result.errorCode} - ${result.errorMessage}"
                    }
                    throw BusinessException(UserErrorCode.USER_INFO_RETRIEVAL_FAILED)
                }
            }
        }

        // 소셜 로그인 마이그레이션 처리
        socialMigrationService.handleUserMigration(keycloakUserInfo = keycloakUserInfo)

        return OAuth2TokenResponse.from(tokenResponse)
    }

    /**
     * OAuth2 state 값을 Redis에 저장
     *
     * CSRF 공격 방어를 위해 생성된 state 값을 Redis에 임시 저장합니다.
     * 회원가입 서비스와 동일한 Redis 패턴을 사용하여 일관성을 유지합니다.
     */
    private fun saveStateToRedis(
        state: String,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        val key = "${authProperties.oauth2.redis.statePrefix}$state"
        val stateData = mapOf(
            "ipAddress" to clientInfo.ipAddress,
            "userAgent" to clientInfo.userAgent,
            "createdAt" to LocalDateTime.now().toString(),
        )

        val jsonData = objectMapper.writeValueAsString(stateData)

        redisTemplate.opsForValue().set(
            key,
            jsonData,
            authProperties.oauth2.stateTtl.toMinutes(),
            TimeUnit.MINUTES,
        )
    }

    /**
     * OAuth2 state 검증 및 소비
     *
     * CSRF 공격 방어를 위해 저장된 state 값을 검증하고 일회성을 보장하기 위해 즉시 삭제합니다.
     */
    private fun validateAndConsumeState(
        receivedState: String?,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        if (receivedState.isNullOrBlank()) {
            logger.warn { "State 파라미터 누락: ip=${clientInfo.ipAddress}" }
            throw BusinessException(UserErrorCode.INVALID_STATE_PARAMETER)
        }

        val key = "${authProperties.oauth2.redis.statePrefix}$receivedState"
        val jsonData = redisTemplate.opsForValue().get(key)
            ?: run {
                logger.warn { "유효하지 않거나 만료된 state: state=$receivedState, ip=${clientInfo.ipAddress}" }
                throw BusinessException(UserErrorCode.INVALID_STATE_PARAMETER)
            }

        // state 사용 후 즉시 삭제 (일회성 보장)
        redisTemplate.delete(key)

        try {
            val stateData = objectMapper.readValue(jsonData, Map::class.java)
            val savedIp = stateData["ipAddress"] as String

            // IP 주소 검증 (선택적 - 더 강한 보안이 필요한 경우)
            if (savedIp != clientInfo.ipAddress) {
                logger.warn {
                    "State IP 불일치 감지: saved=$savedIp, current=${clientInfo.ipAddress}, state=$receivedState"
                }
                // 필요에 따라 예외를 던지거나 경고만 로깅
                // 모바일 환경에서는 IP가 자주 바뀔 수 있으므로 경고만 로깅
            }
        } catch (e: Exception) {
            logger.error { "State 데이터 파싱 오류: state=$receivedState, error=${e.message}" }
            throw BusinessException(UserErrorCode.INVALID_STATE_PARAMETER)
        }
    }
}