package kr.pincoin.api.app.oauth2.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.oauth2.request.OAuth2CallbackRequest
import kr.pincoin.api.app.oauth2.response.OAuth2LoginUrlResponse
import kr.pincoin.api.app.oauth2.response.OAuth2TokenResponse
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTokenService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.ClientUtils
import kr.pincoin.api.global.utils.OAuth2Utils
import org.springframework.stereotype.Service

@Service
class OAuth2Service(
    private val keycloakProperties: KeycloakProperties,
    private val keycloakTokenService: KeycloakTokenService,
    // TODO: 마이그레이션 서비스 의존성 추가
    // private val socialMigrationService: SocialMigrationService,
    // private val keycloakUserService: KeycloakUserService,
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
     * Authorization Code를 Access Token으로 교환 및 마이그레이션 처리
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

        // TODO: state 검증 로직 추가 필요
        // OAuth2Utils.validateState(request.state, expectedState, clientInfo)

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

        // TODO: Access Token으로 Keycloak 사용자 정보 조회
        // val keycloakUserInfo = runBlocking {
        //     keycloakUserService.getUserInfo(tokenResponse.accessToken)
        // }

        // TODO: 소셜 로그인 마이그레이션 처리
        // val migrationResult = socialMigrationService.handleUserMigration(
        //     keycloakUserInfo = keycloakUserInfo,
        //     tokenResponse = tokenResponse
        // )

        // TODO: 마이그레이션 상태에 따른 응답 구성
        // return when (migrationResult.type) {
        //     EXISTING_USER_MIGRATED -> OAuth2TokenResponse.from(tokenResponse, "COMPLETED", "계정이 안전하게 통합되었습니다")
        //     NEW_USER_CREATED -> OAuth2TokenResponse.from(tokenResponse, "NEW_USER", "환영합니다! 새 계정이 생성되었습니다")
        //     ALREADY_MIGRATED -> OAuth2TokenResponse.from(tokenResponse, "EXISTING", null)
        // }

        return OAuth2TokenResponse.from(tokenResponse)
    }
}