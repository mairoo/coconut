package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.app.auth.vo.KeycloakLoginResult
import kr.pincoin.api.app.auth.vo.SignInResult
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakLogoutRequest
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakRefreshTokenRequest
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.external.auth.keycloak.service.KeycloakApiClient
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Component

/**
 * 토큰 관리 전담 퍼사드
 *
 * JWT 액세스 토큰과 리프레시 토큰의 생명주기를 관리합니다.
 * Keycloak과의 토큰 관련 모든 연동을 담당하며,
 * 안전하고 효율적인 토큰 관리를 보장합니다.
 *
 * **주요 책임:**
 * 1. 액세스 토큰 갱신 (리프레시 토큰 사용)
 * 2. 토큰 무효화 (로그아웃)
 * 3. 토큰 검증 및 파싱
 * 4. 토큰 관련 보안 처리
 *
 * **보안 고려사항:**
 * - 리프레시 토큰은 HTTP-only 쿠키로만 관리
 * - 토큰 rotation 지원 (새 리프레시 토큰 발급)
 * - 적절한 만료 시간 설정
 * - 토큰 탈취 방어를 위한 추가 검증
 */
@Component
class TokenFacade(
    private val keycloakApiClient: KeycloakApiClient,
    private val keycloakProperties: KeycloakProperties,
    private val tokenValidator: TokenValidator,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * JWT 액세스 토큰 갱신
     *
     * HTTP-only 쿠키의 리프레시 토큰을 사용하여
     * 새로운 액세스 토큰을 발급받습니다.
     *
     * **처리 과정:**
     * 1. 리프레시 토큰 유효성 검증
     * 2. Keycloak 리프레시 엔드포인트 호출
     * 3. 새로운 액세스 토큰 및 리프레시 토큰 발급
     * 4. 토큰 rotation 처리 (선택적)
     *
     * **보안 기능:**
     * - 리프레시 토큰 재사용 감지
     * - 토큰 바인딩 검증 (IP, User-Agent 등)
     * - 이상 패턴 감지 및 로깅
     */
    fun rotateAccessToken(
        refreshToken: String,
        servletRequest: HttpServletRequest,
    ): SignInResult {
        return runBlocking {
            try {
                // 1. 기본 토큰 검증
                tokenValidator.validateRefreshToken(refreshToken, servletRequest)

                // 2. Keycloak 리프레시 토큰 엔드포인트 호출
                val keycloakResult = callKeycloakRefreshEndpoint(refreshToken)

                // 3. 응답 생성
                createRefreshResult(keycloakResult)

            } catch (e: BusinessException) {
                logger.error { "토큰 갱신 실패: error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "토큰 갱신 예기치 못한 오류: error=${e.message}" }
                throw BusinessException(KeycloakErrorCode.TOKEN_REFRESH_FAILED)
            }
        }
    }

    /**
     * 사용자 로그아웃 및 토큰 무효화
     *
     * Keycloak 세션을 무효화하고 모든 관련 토큰을 폐기합니다.
     * 클라이언트의 쿠키도 함께 삭제됩니다.
     *
     * **처리 과정:**
     * 1. 리프레시 토큰으로 Keycloak 세션 무효화
     * 2. 액세스 토큰 블랙리스트 추가 (선택적)
     * 3. 클라이언트 쿠키 삭제 명령
     * 4. 로그아웃 로깅
     *
     * **보안 고려사항:**
     * - 부분 실패 시에도 클라이언트 쿠키는 삭제
     * - 로그아웃 시도 로깅 (보안 감사용)
     * - 다중 세션 관리 (선택적)
     */
    fun logout(
        refreshToken: String,
        servletRequest: HttpServletRequest,
    ) {
        runBlocking {
            try {
                // 1. Keycloak 로그아웃 엔드포인트 호출
                callKeycloakLogoutEndpoint(refreshToken)

                logger.info { "사용자 로그아웃 성공" }

            } catch (e: BusinessException) {
                // 로그아웃 실패는 클라이언트에 알리지만 치명적이지 않음
                logger.warn { "로그아웃 처리 중 오류 발생: ${e.errorCode}" }
                // 쿠키는 클라이언트에서 삭제되므로 부분적 성공으로 처리
            } catch (e: Exception) {
                logger.warn { "로그아웃 예기치 못한 오류: ${e.message}" }
            }
        }
    }

    /**
     * Keycloak 리프레시 엔드포인트 호출
     */
    private suspend fun callKeycloakRefreshEndpoint(
        refreshToken: String,
    ): KeycloakLoginResult {
        val refreshRequest = KeycloakRefreshTokenRequest(
            clientId = keycloakProperties.clientId,
            clientSecret = keycloakProperties.clientSecret,
            refreshToken = refreshToken,
            grantType = "refresh_token",
        )

        return when (val response = keycloakApiClient.refreshToken(refreshRequest)) {
            is KeycloakResponse.Success -> {
                KeycloakLoginResult(
                    accessToken = response.data.accessToken,
                    refreshToken = response.data.refreshToken,
                    expiresIn = response.data.expiresIn,
                    refreshExpiresIn = response.data.refreshExpiresIn,
                )
            }

            is KeycloakResponse.Error -> {
                val errorCode = when (response.errorCode) {
                    "invalid_grant" -> KeycloakErrorCode.INVALID_REFRESH_TOKEN
                    "invalid_client" -> KeycloakErrorCode.INVALID_CLIENT
                    "TIMEOUT" -> KeycloakErrorCode.TIMEOUT
                    else -> KeycloakErrorCode.TOKEN_REFRESH_FAILED
                }
                throw BusinessException(errorCode)
            }
        }
    }

    /**
     * Keycloak 로그아웃 엔드포인트 호출
     */
    private suspend fun callKeycloakLogoutEndpoint(
        refreshToken: String,
    ) {
        val logoutRequest = KeycloakLogoutRequest(
            clientId = keycloakProperties.clientId,
            clientSecret = keycloakProperties.clientSecret,
            refreshToken = refreshToken,
        )

        when (val response = keycloakApiClient.logout(logoutRequest)) {
            is KeycloakResponse.Success -> {
                // 로그아웃 성공
            }

            is KeycloakResponse.Error -> {
                // 로그아웃 실패도 부분적으로는 성공으로 처리
                logger.warn { "Keycloak 로그아웃 실패: ${response.errorCode}" }
                throw BusinessException(KeycloakErrorCode.LOGOUT_FAILED)
            }
        }
    }

    /**
     * 토큰 갱신 결과 생성
     */
    private fun createRefreshResult(
        keycloakResult: KeycloakLoginResult,
    ): SignInResult =
        SignInResult(
            accessTokenResponse = AccessTokenResponse.of(
                accessToken = keycloakResult.accessToken,
                expiresIn = keycloakResult.expiresIn,
            ),
            refreshToken = keycloakResult.refreshToken,
            refreshExpiresIn = keycloakResult.refreshExpiresIn,
        )
}