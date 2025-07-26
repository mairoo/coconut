package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.app.auth.vo.KeycloakLoginResult
import kr.pincoin.api.app.auth.vo.SignInResult
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakCreateUserRequest
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakLoginRequest
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.external.auth.keycloak.service.KeycloakApiClient
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.security.encoder.DjangoPasswordEncoder
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * 로그인 프로세스 전체 조율 퍼사드
 *
 * Keycloak 우선 인증과 레거시 사용자 마이그레이션을 지원하는
 * 3단계 로그인 프로세스를 관리합니다.
 *
 * **1단계 - Keycloak 우선 인증:**
 * - 사용자 이메일/패스워드로 Keycloak 인증 시도
 * - 성공 시: JWT 토큰 반환 (최종 완료)
 * - 실패 시: 2단계로 진행
 *
 * **2단계 - 레거시 사용자 검증:**
 * - 기존 User 테이블에서 사용자 조회
 * - 레거시 패스워드 인코더(PBKDF2)로 비밀번호 검증
 * - 검증 실패 시: 인증 오류 반환
 * - 검증 성공 시: 3단계로 진행
 *
 * **3단계 - Keycloak 마이그레이션:**
 * - 레거시 사용자를 Keycloak에 생성
 * - User 테이블의 keycloak_id 업데이트
 * - 새로운 Keycloak 계정으로 JWT 토큰 발급
 */
@Component
class SignInFacade(
    private val signInValidator: SignInValidator,
    private val userService: UserService,
    private val keycloakApiClient: KeycloakApiClient,
    private val keycloakProperties: KeycloakProperties,
    private val authKeycloakService: AuthKeycloakService,
    private val djangoPasswordEncoder: DjangoPasswordEncoder,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 로그인 프로세스 실행
     *
     * 3단계 로그인 프로세스를 통해 Keycloak 우선 인증과
     * 레거시 사용자 마이그레이션을 지원합니다.
     *
     * @param request 로그인 요청 정보
     * @param httpServletRequest HTTP 요청 정보
     * @return 로그인 응답 (JWT 토큰)
     * @throws BusinessException 인증 실패, 시스템 오류 등
     */
    @Transactional
    fun processSignIn(
        request: SignInRequest,
        httpServletRequest: HttpServletRequest,
    ): SignInResult {
        return runBlocking {
            try {
                // 0. 기본 보안 검증 (reCAPTCHA 등)
                signInValidator.validateSignInRequest(request, httpServletRequest)

                // 1단계: Keycloak 우선 인증 시도
                try {
                    val keycloakResult = attemptKeycloakLogin(request)

                    return@runBlocking createSignInResult(keycloakResult)
                } catch (e: BusinessException) {
                    when (e.errorCode) {
                        KeycloakErrorCode.INVALID_CREDENTIALS -> {
                            // 2단계로 진행
                        }

                        else -> {
                            logger.error { "Keycloak 인증 시스템 오류: email=${request.email}, error=${e.errorCode}" }
                            throw e
                        }
                    }
                }

                // 2단계: 레거시 사용자 검증
                val legacyUser = findAndValidateLegacyUser(request)
                    ?: throw BusinessException(UserErrorCode.INVALID_CREDENTIALS)

                // 3단계: Keycloak 마이그레이션
                val migratedResult = migrateLegacyUserToKeycloak(legacyUser, request)

                createSignInResult(migratedResult)

            } catch (e: BusinessException) {
                logger.error { "로그인 오류: email=${request.email}, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "로그인 예기치 못한 오류: email=${request.email}, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * 1단계: Keycloak 우선 인증 시도
     */
    private suspend fun attemptKeycloakLogin(
        request: SignInRequest,
    ): KeycloakLoginResult {
        val loginRequest = KeycloakLoginRequest(
            clientId = keycloakProperties.clientId,
            clientSecret = keycloakProperties.clientSecret,
            username = request.email,
            password = request.password,
            grantType = "password",
            scope = "openid profile email",
        )

        return when (val response = keycloakApiClient.login(loginRequest)) {
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
                    "invalid_grant", "invalid_client" -> KeycloakErrorCode.INVALID_CREDENTIALS
                    "TIMEOUT" -> KeycloakErrorCode.TIMEOUT
                    else -> KeycloakErrorCode.UNKNOWN
                }
                throw BusinessException(errorCode)
            }
        }
    }

    /**
     * 2단계: 레거시 사용자 검증
     */
    private fun findAndValidateLegacyUser(
        request: SignInRequest,
    ): User? {
        return try {
            val user = userService.findUser(
                UserSearchCriteria(email = request.email, isActive = true)
            )

            // Keycloak ID가 없는 경우만 레거시 사용자로 간주
            if (user.keycloakId != null) {
                logger.warn { "Keycloak ID가 있는 사용자인데 Keycloak 인증 실패: email=${request.email}" }
                return null
            }

            // 레거시 패스워드 검증 (PBKDF2)
            if (djangoPasswordEncoder.matches(request.password, user.password)) {
                user
            } else {
                logger.warn { "레거시 사용자 비밀번호 검증 실패: email=${request.email}" }
                null
            }

        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.NOT_FOUND -> {
                    logger.warn { "사용자 없음: email=${request.email}" }
                    null
                }

                else -> throw e
            }
        }
    }

    /**
     * 3단계: 레거시 사용자 Keycloak 마이그레이션
     */
    private suspend fun migrateLegacyUserToKeycloak(
        user: User,
        request: SignInRequest,
    ): KeycloakLoginResult {
        // Admin 토큰 획득
        val adminToken = authKeycloakService.getAdminToken()

        // Keycloak에 사용자 생성
        val keycloakUserId = createKeycloakUserForMigration(user, request, adminToken)

        // DB에 Keycloak ID 업데이트
        userService.linkKeycloak(user.id!!, UUID.fromString(keycloakUserId))

        // 새로 생성된 Keycloak 계정으로 로그인하여 토큰 발급
        return attemptKeycloakLogin(request)
    }

    /**
     * 마이그레이션용 Keycloak 사용자 생성
     */
    private suspend fun createKeycloakUserForMigration(
        user: User,
        request: SignInRequest,
        adminToken: String,
    ): String {
        val createUserRequest = KeycloakCreateUserRequest(
            username = user.email,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            enabled = true,
            emailVerified = true, // 기존 사용자이므로 이메일 인증됨으로 설정
            credentials = listOf(
                KeycloakCreateUserRequest.KeycloakCredential(
                    type = "password",
                    value = request.password, // 사용자가 입력한 비밀번호로 설정
                    temporary = false,
                )
            )
        )

        return when (val response = keycloakApiClient.createUser(adminToken, createUserRequest)) {
            is KeycloakResponse.Success -> {
                response.data.userId
            }

            is KeycloakResponse.Error -> {
                logger.error { "마이그레이션용 Keycloak 사용자 생성 실패: email=${user.email}, error=${response.errorCode}" }

                val errorCode = when (response.errorCode) {
                    "USER_EXISTS" -> UserErrorCode.EMAIL_ALREADY_EXISTS
                    "TIMEOUT" -> KeycloakErrorCode.TIMEOUT
                    "SYSTEM_ERROR" -> KeycloakErrorCode.SYSTEM_ERROR
                    else -> KeycloakErrorCode.UNKNOWN
                }

                throw BusinessException(errorCode)
            }
        }
    }

    /**
     * 로그인 결과 생성
     */
    private fun createSignInResult(
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