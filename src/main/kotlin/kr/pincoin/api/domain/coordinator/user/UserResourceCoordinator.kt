package kr.pincoin.api.domain.coordinator.user

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
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
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class UserResourceCoordinator(
    private val userService: UserService,
    private val keycloakApiClient: KeycloakApiClient,
    private val keycloakProperties: KeycloakProperties
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Keycloak과 DB에 동시에 사용자 생성
     * 보상 트랜잭션으로 일관성 보장
     */
    @Transactional
    suspend fun createUserWithKeycloak(
        request: SignUpRequest,
        adminToken: String
    ): User = withContext(Dispatchers.IO) {
        var keycloakUserId: String? = null

        try {
            // 1. 이메일 중복 검증 (Keycloak 생성 전 사전 검증)
            validateEmailNotExists(request.email)

            // 2. Keycloak에 사용자 생성
            keycloakUserId = createKeycloakUser(request, adminToken)
            val keycloakUuid = UUID.fromString(keycloakUserId)

            // 3. DB에 사용자 생성 (Keycloak ID 연결)
            val user = userService.createUser(request, keycloakUuid)
            user

        } catch (_: IllegalArgumentException) {
            // UUID 변환 실패시
            logger.error { "Keycloak ID를 UUID로 변환 실패: keycloakUserId=$keycloakUserId" }
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, adminToken, request.email)
            }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        } catch (e: BusinessException) {
            logger.error { "사용자 생성 오류: email=${request.email}, error=$e" }

            // DB 생성 실패시 Keycloak 사용자 삭제 (보상 트랜잭션)
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, adminToken, request.email)
            }

            throw e
        } catch (e: Exception) {
            logger.error { "사용자 생성 시스템 오류: email=${request.email}, error=$e" }

            // DB 생성 실패시 Keycloak 사용자 삭제 (보상 트랜잭션)
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, adminToken, request.email)
            }

            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 사용자 인증 및 토큰 발급
     */
    suspend fun authenticateUser(email: String, password: String): AccessTokenResponse = withContext(Dispatchers.IO) {
        val loginRequest = KeycloakLoginRequest(
            clientId = keycloakProperties.clientId,
            clientSecret = keycloakProperties.clientSecret,
            username = email,
            password = password,
            grantType = "password",
            scope = "openid profile email",
        )

        return@withContext when (val response = keycloakApiClient.login(loginRequest)) {
            is KeycloakResponse.Success -> {
                val tokenData = response.data
                AccessTokenResponse.of(
                    accessToken = tokenData.accessToken,
                    expiresIn = tokenData.expiresIn,
                )
            }

            is KeycloakResponse.Error -> {
                logger.error { "사용자 인증 실패: email=$email, error=${response.errorCode}" }

                val errorCode = when (response.errorCode) {
                    "invalid_grant" -> KeycloakErrorCode.INVALID_CREDENTIALS
                    "invalid_client" -> KeycloakErrorCode.INVALID_CREDENTIALS
                    "TIMEOUT" -> KeycloakErrorCode.TIMEOUT
                    else -> KeycloakErrorCode.UNKNOWN
                }

                throw BusinessException(errorCode)
            }
        }
    }

    /**
     * 이메일 중복 검증
     */
    private fun validateEmailNotExists(email: String) {
        try {
            userService.findUser(UserSearchCriteria(email = email, isActive = true))
            // 사용자가 발견되면 중복 이메일
            logger.warn { "이미 가입된 이메일: $email" }
            throw BusinessException(UserErrorCode.EMAIL_ALREADY_EXISTS)
        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.NOT_FOUND -> {
                    // 사용자가 없으면 정상 (가입 가능)
                    logger.debug { "이메일 중복 검증 통과: $email" }
                }

                else -> throw e // 다른 에러는 그대로 전파
            }
        }
    }

    /**
     * Keycloak에 사용자 생성
     */
    private suspend fun createKeycloakUser(
        request: SignUpRequest,
        adminToken: String
    ): String {
        val createUserRequest = KeycloakCreateUserRequest(
            username = request.email,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            enabled = true,
            emailVerified = true, // 이메일 인증 완료 후 호출되므로 true로 설정
            credentials = listOf(
                KeycloakCreateUserRequest.KeycloakCredential(
                    type = "password",
                    value = request.password,
                    temporary = false
                )
            )
        )

        return when (val response = keycloakApiClient.createUser(adminToken, createUserRequest)) {
            is KeycloakResponse.Success -> {
                response.data.userId
            }

            is KeycloakResponse.Error -> {
                logger.error { "Keycloak 사용자 생성 실패: email=${request.email}, error=${response.errorCode}" }

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
     * 보상 트랜잭션: Keycloak에서 사용자 삭제
     * DB 생성 실패시 Keycloak에 생성된 사용자를 정리
     */
    private suspend fun executeCompensatingTransaction(
        keycloakUserId: String,
        adminToken: String,
        email: String
    ) {
        logger.warn { "보상 트랜잭션 시작: Keycloak 사용자 삭제 - userId=$keycloakUserId, email=$email" }

        try {
            when (val response = keycloakApiClient.deleteUser(adminToken, keycloakUserId)) {
                is KeycloakResponse.Success -> {
                }

                is KeycloakResponse.Error -> {
                    logger.error {
                        "보상 트랜잭션 실패: Keycloak 사용자 삭제 오류 - userId=$keycloakUserId, error=${response.errorCode}"
                    }
                    // 보상 트랜잭션 실패는 별도 알림/모니터링이 필요할 수 있음
                    notifyCompensationFailure(keycloakUserId, email, response.errorCode)
                }
            }
        } catch (e: Exception) {
            logger.error {
                "보상 트랜잭션 예외 발생: userId=$keycloakUserId, email=$email, error=${e.message}"
            }
            notifyCompensationFailure(keycloakUserId, email, e.message ?: "UNKNOWN_ERROR")
        }
    }

    /**
     * 보상 트랜잭션 실패 알림
     * 실제 운영환경에서는 모니터링 시스템이나 알림 시스템과 연동
     */
    private fun notifyCompensationFailure(keycloakUserId: String, email: String, errorMessage: String) {
        logger.error {
            "긴급: 보상 트랜잭션 실패로 인한 데이터 불일치 발생 - " +
                    "keycloakUserId=$keycloakUserId, email=$email, error=$errorMessage"
        }

        // TODO: 실제 구현시 다음과 같은 처리 필요
        // 1. 모니터링 시스템 알림 (예: Sentry, Datadog 등)
        // 2. 관리자 이메일/슬랙 알림
        // 3. 수동 정리가 필요한 데이터 목록에 추가
        // 4. 배치 작업으로 주기적 정리 대상에 추가
    }
}