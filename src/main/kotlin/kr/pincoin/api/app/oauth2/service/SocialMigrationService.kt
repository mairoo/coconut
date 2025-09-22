package kr.pincoin.api.app.oauth2.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.oauth2.response.SocialMigrationResponse
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakUserInfoResponse
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

/**
 * 소셜 로그인 사용자 마이그레이션 서비스
 *
 * OAuth2/OIDC를 통한 소셜 로그인 시 기존 레거시 사용자를 Keycloak과 연동하는 마이그레이션을 처리합니다.
 *
 * **케이스별 처리 로직:**
 * 1. user.email(O) + user.password(O) + user.keycloakId(O) → 비정상 상태 (데이터 정합성 오류)
 * 2. user.email(O) + user.password(O) + user.keycloakId(X) → 마이그레이션 필요 안내
 * 3. user.email(O) + user.password(X) + user.keycloakId(O) → 정상 로그인 (이미 마이그레이션 완료)
 * 4. user.email(O) + user.password(X) + user.keycloakId(X) → 소셜 전용 사용자, Keycloak 연동 처리
 * 5. user.email(X) → 신규 사용자 생성
 *
 * **보안 고려사항:**
 * - 이메일 검증된 소셜 계정만 마이그레이션 허용
 * - 데이터 정합성 검증을 통한 비정상 상태 감지
 */
@Service
@Transactional(readOnly = true)
class SocialMigrationService(
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 소셜 로그인 사용자 마이그레이션 (필요 시)
     */
    @Transactional
    fun handleUserMigration(
        keycloakUserInfo: KeycloakUserInfoResponse,
    ) =
        try {
            // 0. 사전 검증
            val email = validateAndExtractEmail(keycloakUserInfo)

            // 1. 기존 사용자 조회
            val existingUser = findExistingUser(email)

            // 2. 케이스별 처리
            when {
                // 케이스 5: 사용자 없음 → 신규 사용자 생성
                existingUser == null -> {
                    logger.info { "케이스 5 - 신규 소셜 사용자: email=$email" }
                    handleNewUser(keycloakUserInfo, email)
                }

                // 케이스 1: password(O) + keycloakId(O) → 비정상 상태
                existingUser.hasPassword() && existingUser.hasKeycloakId() -> {
                    logger.error { "케이스 1 - 데이터 정합성 오류: email=$email, userId=${existingUser.id}" }
                    throw BusinessException(UserErrorCode.DATA_INTEGRITY_ERROR)
                }

                // 케이스 2: password(O) + keycloakId(X) → 마이그레이션 필요
                existingUser.hasPassword() && !existingUser.hasKeycloakId() -> {
                    logger.warn { "케이스 2 - 마이그레이션 필요: email=$email, userId=${existingUser.id}" }
                    throw BusinessException(UserErrorCode.MIGRATION_REQUIRED)
                }

                // 케이스 3: password(X) + keycloakId(O) → 정상 로그인
                !existingUser.hasPassword() && existingUser.hasKeycloakId() -> {
                    // 별도 처리 불필요, 이미 마이그레이션 완료된 상태
                }

                // 케이스 4: password(X) + keycloakId(X) → 소셜 전용 사용자 Keycloak 연동
                !existingUser.hasPassword() && !existingUser.hasKeycloakId() -> {
                    // Keycloak 연동 이외 별도 소셜 계정 데이터 저장 없음 = Keycloak 책임
                    // - socialaccount_socialaccount
                    // - socialaccount_socialtoken
                    // - socialaccount_socialapp
                    handleSocialOnlyUserLink(existingUser, keycloakUserInfo)
                }

                // 예상하지 못한 케이스 (방어 코드)
                else -> {
                    logger.error { "예상하지 못한 사용자 상태: email=$email, userId=${existingUser.id}" }
                    throw BusinessException(UserErrorCode.SYSTEM_ERROR)
                }
            }
        } catch (e: BusinessException) {
            val email = keycloakUserInfo.email ?: "unknown"
            logger.error { "소셜 마이그레이션 오류: email=$email, error=${e.errorCode}" }
            throw e
        } catch (e: Exception) {
            val email = keycloakUserInfo.email ?: "unknown"
            logger.error { "소셜 마이그레이션 예기치 못한 오류: email=$email, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }

    /**
     * 이메일 검증 및 추출
     *
     * 소셜 로그인을 통한 마이그레이션은 이메일이 검증된 계정만 허용합니다.
     */
    private fun validateAndExtractEmail(
        keycloakUserInfo: KeycloakUserInfoResponse,
    ): String {
        // 이메일 존재 여부 확인
        val email = keycloakUserInfo.email
            ?: throw BusinessException(UserErrorCode.EMAIL_NOT_PROVIDED)

        // 이메일 검증 상태 확인
        if (!keycloakUserInfo.emailVerified) {
            logger.warn { "이메일 미검증 소셜 계정 마이그레이션 시도: email=$email" }
            throw BusinessException(UserErrorCode.EMAIL_NOT_VERIFIED)
        }

        return email
    }

    /**
     * 기존 사용자 조회
     */
    private fun findExistingUser(
        email: String,
    ): User? =
        try {
            userService.findUser(
                UserSearchCriteria(email = email, isActive = true)
            )
        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.NOT_FOUND -> null
                else -> throw e
            }
        }

    /**
     * 케이스 5: 신규 사용자 생성
     *
     * 완전히 새로운 소셜 로그인 사용자를 생성합니다.
     */
    private fun handleNewUser(
        keycloakUserInfo: KeycloakUserInfoResponse,
        email: String,
    ) {
        val keycloakId = UUID.fromString(keycloakUserInfo.sub)
        val request = createSignUpRequestFromKeycloak(keycloakUserInfo, email)

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
            keycloakId = keycloakId,
        )

        userService.save(user)
    }

    /**
     * 케이스 4: 소셜 전용 사용자 Keycloak 연동
     *
     * 레거시 서비스에서 소셜 로그인만 사용했던 사용자의 Keycloak 연동을 처리합니다.
     */
    private fun handleSocialOnlyUserLink(
        existingUser: User,
        keycloakUserInfo: KeycloakUserInfoResponse
    ) {
        val keycloakId = UUID.fromString(keycloakUserInfo.sub)

        userService.linkKeycloak(
            userId = existingUser.id!!,
            keycloakId = keycloakId,
            clearPassword = false, // 이미 패스워드가 없으므로 clearPassword 불필요
        )
    }

    /**
     * Keycloak 사용자 정보를 SignUpRequest로 변환
     */
    private fun createSignUpRequestFromKeycloak(
        keycloakUserInfo: KeycloakUserInfoResponse,
        email: String,
    ): SignUpRequest =
        SignUpRequest(
            username = keycloakUserInfo.preferredUsername,
            email = email,
            firstName = keycloakUserInfo.givenName ?: "",
            lastName = keycloakUserInfo.familyName ?: "",
            password = "", // 소셜 로그인이므로 패스워드 불필요
            recaptchaToken = "", // 이미 Keycloak에서 검증된 사용자이므로 불필요
        )

    /**
     * User 엔티티 확장 함수들
     */
    private fun User.hasPassword(
    ): Boolean =
        password.isNotBlank()

    private fun User.hasKeycloakId(
    ): Boolean =
        keycloakId != null

    // SocialMigrationService.kt에 추가할 메소드들

    /**
     * JWT 토큰에서 사용자 정보 추출하여 마이그레이션 처리
     *
     * NextAuth.js에서 받은 Keycloak JWT 토큰을 기반으로 마이그레이션을 수행합니다.
     */
    @PreAuthorize("hasAuthority('SCOPE_openid')")
    @Transactional
    fun handleUserMigrationFromJwt(
        jwt: Jwt
    ): SocialMigrationResponse {
        // JWT에서 Keycloak 사용자 정보 추출
        val keycloakUserInfo = extractKeycloakUserInfoFromJwt(jwt)

        try {
            // 기존 마이그레이션 로직 재사용
            handleUserMigration(keycloakUserInfo)

            // 성공적인 마이그레이션 후 응답 생성
            return createMigrationResponse(keycloakUserInfo)

        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.MIGRATION_REQUIRED -> {
                    // 케이스 2: 수동 마이그레이션 필요
                    val existingUser = findExistingUser(keycloakUserInfo.email!!)
                    return SocialMigrationResponse.migrationRequired(
                        userId = existingUser!!.id!!,
                        email = existingUser.email
                    )
                }

                else -> throw e
            }
        }
    }

    /**
     * JWT 토큰에서 Keycloak 사용자 정보 추출
     */
    private fun extractKeycloakUserInfoFromJwt(
        jwt: Jwt
    ): KeycloakUserInfoResponse {
        return KeycloakUserInfoResponse(
            sub = jwt.getClaimAsString("sub")
                ?: throw BusinessException(UserErrorCode.USER_INFO_RETRIEVAL_FAILED),
            email = jwt.getClaimAsString("email"),
            emailVerified = jwt.getClaimAsBoolean("email_verified") ?: false,
            preferredUsername = jwt.getClaimAsString("preferred_username")
                ?: jwt.getClaimAsString("username"),
            givenName = jwt.getClaimAsString("given_name"),
            familyName = jwt.getClaimAsString("family_name"),
            name = jwt.getClaimAsString("name")
        )
    }

    /**
     * 마이그레이션 완료 후 응답 생성
     */
    private fun createMigrationResponse(
        keycloakUserInfo: KeycloakUserInfoResponse
    ): SocialMigrationResponse {
        val email = keycloakUserInfo.email!!
        val user = findExistingUser(email)!!
        val keycloakId = keycloakUserInfo.sub

        return when {
            // 신규 사용자인 경우 (방금 생성됨)
            user.keycloakId.toString() == keycloakId && user.dateJoined.isAfter(
                LocalDateTime.now().minusMinutes(1)
            ) -> {
                SocialMigrationResponse.newUser(
                    userId = user.id!!,
                    email = email,
                    keycloakId = keycloakId
                )
            }

            // 기존 소셜 전용 사용자와 연동된 경우
            user.keycloakId.toString() == keycloakId && !user.hasPassword() -> {
                SocialMigrationResponse.linked(
                    userId = user.id!!,
                    email = email,
                    keycloakId = keycloakId
                )
            }

            // 이미 연동된 사용자
            else -> {
                SocialMigrationResponse.alreadyLinked(
                    userId = user.id!!,
                    email = email,
                    keycloakId = keycloakId
                )
            }
        }
    }
}