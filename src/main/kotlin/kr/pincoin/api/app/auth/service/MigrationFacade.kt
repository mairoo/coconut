package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.auth.request.MigrationRequest
import kr.pincoin.api.app.auth.response.MigrationResponse
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakUserService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.security.encoder.DjangoPasswordEncoder
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * 레거시 사용자 마이그레이션 프로세스 전체 조율 퍼사드
 *
 * Django allauth 기반 레거시 사용자를 Keycloak으로 마이그레이션하는
 * 전체 프로세스를 관리합니다.
 *
 * **마이그레이션 프로세스:**
 * 1. 보안 검증 (reCAPTCHA 등)
 * 2. 레거시 사용자 확인 및 비밀번호 검증
 * 3. 이미 마이그레이션된 사용자 체크
 * 4. Keycloak 사용자 생성 및 DB 연결
 *
 * **예외 처리:**
 * - 사용자 없음/비밀번호 틀림 → INVALID_CREDENTIALS
 * - 이미 마이그레이션 완료 → ALREADY_MIGRATED
 * - Keycloak 연동 실패 → 적절한 Keycloak 에러 코드
 */
@Component
class MigrationFacade(
    private val migrationValidator: MigrationValidator,
    private val userService: UserService,
    private val keycloakUserService: KeycloakUserService,
    private val djangoPasswordEncoder: DjangoPasswordEncoder,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 레거시 사용자 마이그레이션 프로세스 실행
     *
     * Django allauth 기반 레거시 사용자를 Keycloak으로 마이그레이션합니다.
     *
     * @param request 마이그레이션 요청 정보 (이메일, 비밀번호, reCAPTCHA)
     * @param httpServletRequest HTTP 요청 정보
     * @return 마이그레이션 완료 응답
     * @throws BusinessException 마이그레이션 실패, 이미 완료된 경우, 사용자 없음 등
     */
    @Transactional
    fun processMigration(
        request: MigrationRequest,
        httpServletRequest: HttpServletRequest,
    ): MigrationResponse {
        return runBlocking {
            try {
                // 1. 마이그레이션 보안 검증 (reCAPTCHA)
                migrationValidator.validateMigrationRequest(request, httpServletRequest)

                // 2. 레거시 사용자 확인 및 검증
                val legacyUser = findAndValidateLegacyUser(request)
                    ?: throw BusinessException(UserErrorCode.INVALID_CREDENTIALS)

                // 3. 이미 마이그레이션된 사용자 확인
                if (legacyUser.keycloakId != null) {
                    logger.info { "이미 마이그레이션 완료된 사용자: email=${request.email}" }
                    throw BusinessException(UserErrorCode.ALREADY_MIGRATED)
                }

                // 4. Keycloak 마이그레이션 수행
                val migratedAt = performKeycloakMigration(legacyUser, request)

                MigrationResponse.of(
                    email = request.email,
                    migratedAt = migratedAt
                )

            } catch (e: BusinessException) {
                logger.error { "마이그레이션 오류: email=${request.email}, error=${e.errorCode}" }
                throw e
            } catch (e: Exception) {
                logger.error { "마이그레이션 예기치 못한 오류: email=${request.email}, error=${e.message}" }
                throw BusinessException(UserErrorCode.SYSTEM_ERROR)
            }
        }
    }

    /**
     * 레거시 사용자 확인 및 비밀번호 검증
     */
    private fun findAndValidateLegacyUser(
        request: MigrationRequest,
    ): User? {
        return try {
            val user = userService.findUser(
                UserSearchCriteria(email = request.email, isActive = true)
            )

            // 이미 마이그레이션된 사용자 체크 (빈 패스워드)
            if (user.password.isBlank() || user.keycloakId != null) {
                logger.warn { "이미 마이그레이션된 사용자" }
                return null
            }

            // 레거시 패스워드 검증 (PBKDF2)
            try {
                if (djangoPasswordEncoder.matches(request.password, user.password)) {
                    user
                } else {
                    logger.warn { "레거시 사용자 비밀번호 검증 실패" }
                    null
                }
            } catch (e: Exception) {
                // Django 패스워드 검증 자체에서 예외 발생 시 (잘못된 형식 등)
                logger.warn(e) { "레거시 패스워드 검증 중 예외 발생" }
                null
            }

        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.NOT_FOUND -> {
                    logger.warn { "마이그레이션 대상 사용자 없음: email=${request.email}" }
                    null
                }

                else -> throw e
            }
        }
    }

    /**
     * Keycloak 마이그레이션 수행
     */
    private suspend fun performKeycloakMigration(
        user: User,
        request: MigrationRequest,
    ): LocalDateTime {
        // Keycloak에 사용자 생성
        val keycloakUserId = createKeycloakUserForMigration(user, request)

        // DB에 Keycloak ID 업데이트
        userService.linkKeycloak(user.id!!, UUID.fromString(keycloakUserId), true)

        return LocalDateTime.now()
    }

    /**
     * 마이그레이션용 Keycloak 사용자 생성
     */
    private suspend fun createKeycloakUserForMigration(
        user: User,
        request: MigrationRequest,
    ): String {
        return when (val response = keycloakUserService.createUser(
            username = user.email,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            password = request.password,
            enabled = true,
        )) {
            is KeycloakResponse.Success -> {
                logger.info { "마이그레이션용 Keycloak 사용자 생성 성공: email=${user.email}" }
                response.data.userId
            }

            is KeycloakResponse.Error -> {
                logger.error {
                    "마이그레이션용 Keycloak 사용자 생성 실패: email=${user.email}, " +
                            "keycloakError=${response.errorCode}, keycloakMessage=${response.errorMessage}"
                }

                // 간단한 에러 코드 매핑만
                val errorCode = when (response.errorCode) {
                    "USER_EXISTS", "CONFLICT" -> UserErrorCode.EMAIL_ALREADY_EXISTS
                    "TIMEOUT" -> KeycloakErrorCode.TIMEOUT
                    "SYSTEM_ERROR" -> KeycloakErrorCode.SYSTEM_ERROR
                    else -> KeycloakErrorCode.UNKNOWN
                }

                throw BusinessException(errorCode)
            }
        }
    }
}