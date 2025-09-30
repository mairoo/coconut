package kr.pincoin.api.domain.coordinator.user

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.Profile
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.ProfileService
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.error.KeycloakErrorCode
import kr.pincoin.api.external.auth.keycloak.service.KeycloakUserService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class UserResourceCoordinator(
    private val userService: UserService,
    private val profileService: ProfileService,
    private val keycloakUserService: KeycloakUserService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Keycloak과 DB에 동시에 사용자 생성
     * 보상 트랜잭션으로 일관성 보장
     */
    @Transactional
    suspend fun createUserWithKeycloak(
        request: SignUpRequest
    ): User = withContext(Dispatchers.IO) {
        var keycloakUserId: String? = null

        try {
            // 1. Keycloak에 사용자 생성
            keycloakUserId = createKeycloakUser(request)
            val keycloakUuid = UUID.fromString(keycloakUserId)

            // 2. DB에 사용자 생성 (Keycloak ID 연결)
            val user = User.of(
                password = "",
                lastLogin = null,
                isSuperuser = false,
                username = request.email,
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
                isStaff = false,
                isActive = true,
                dateJoined = LocalDateTime.now(),
                keycloakId = keycloakUuid,
            )
            val savedUser = userService.save(user)

            // 3. Profile 생성
            val profile = Profile.of(
                userId = savedUser.id!!,
                address = "",
                phone = null,
                phoneVerified = false,
                phoneVerifiedStatus = 0,
                dateOfBirth = null,
                domestic = 0,
                gender = 0,
                telecom = "",
                photoId = "",
                card = "",
                documentVerified = false,
                totalOrderCount = 0,
                firstPurchased = null,
                lastPurchased = null,
                maxPrice = BigDecimal.ZERO,
                averagePrice = BigDecimal.ZERO,
                totalListPrice = BigDecimal.ZERO,
                totalSellingPrice = BigDecimal.ZERO,
                notPurchasedMonths = false,
                repurchased = null,
                memo = "",
                mileage = BigDecimal.ZERO,
                allowOrder = true,
            )
            profileService.save(profile)
            savedUser

        } catch (_: IllegalArgumentException) {
            // UUID 변환 실패시
            logger.error { "Keycloak ID를 UUID로 변환 실패: keycloakUserId=$keycloakUserId" }
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, request.email)
            }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        } catch (e: BusinessException) {
            logger.error { "사용자 생성 오류: email=${request.email}, error=$e" }

            // DB 생성 실패시 Keycloak 사용자 삭제 (보상 트랜잭션)
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, request.email)
            }

            throw e
        } catch (e: Exception) {
            logger.error { "사용자 생성 시스템 오류: email=${request.email}, error=$e" }

            // DB 생성 실패시 Keycloak 사용자 삭제 (보상 트랜잭션)
            keycloakUserId?.let { userId ->
                executeCompensatingTransaction(userId, request.email)
            }

            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * Keycloak에 사용자 생성
     */
    private suspend fun createKeycloakUser(
        request: SignUpRequest
    ): String {
        return when (val response = keycloakUserService.createUser(
            username = request.email,
            email = request.email,
            firstName = request.firstName,
            lastName = request.lastName,
            password = request.password,
            enabled = true,
            emailVerified = true, // 백엔드에서 이미 이메일 인증 완료
        )) {
            is KeycloakResponse.Success -> {
                response.data.userId
            }

            is KeycloakResponse.Error -> {
                logger.error { "Keycloak 사용자 생성 실패: email=${request.email}, error=${response.errorCode}" }

                val errorCode = when (response.errorCode) {
                    "USER_EXISTS" -> {
                        logger.warn { "예상치 못한 Keycloak 사용자 중복: email=${request.email}" }
                        UserErrorCode.EMAIL_ALREADY_EXISTS
                    }

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
        email: String
    ) {
        logger.warn { "보상 트랜잭션 시작: Keycloak 사용자 삭제 - userId=$keycloakUserId, email=$email" }

        try {
            when (val response = keycloakUserService.deleteUser(keycloakUserId)) {
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