package kr.pincoin.api.domain.user.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun save(
        request: AdminUserCreateRequest,
        keycloakId: UUID,
    ): User {
        try {
            val user = User.of(
                password = "",
                lastLogin = null,
                isSuperuser = request.isSuperuser,
                username = request.username,
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
                isStaff = request.isStaff,
                isActive = request.isActive,
                dateJoined = LocalDateTime.now(),
                keycloakId = keycloakId,
            )

            return userRepository.save(user)
        } catch (_: DataIntegrityViolationException) {
            logger.error { "사용자 중복: email=${request.email}" }
            throw BusinessException(UserErrorCode.ALREADY_EXISTS)
        }
    }

    @Transactional
    fun save(
        request: SignUpRequest,
        keycloakId: UUID,
    ): User {
        try {
            val user = User.of(
                password = "",
                lastLogin = null,
                isSuperuser = false,
                username = request.username,
                firstName = request.firstName,
                lastName = request.lastName,
                email = request.email,
                isStaff = false,
                isActive = true,
                dateJoined = LocalDateTime.now(),
                keycloakId = keycloakId,
            )

            return userRepository.save(user)
        } catch (_: DataIntegrityViolationException) {
            logger.error { "사용자 중복: email=${request.email}" }
            throw BusinessException(UserErrorCode.ALREADY_EXISTS)
        } catch (e: Exception) {
            logger.error { "사용자 생성 중 예외 발생: email=${request.email}, error=${e.message}" }
            throw e
        }
    }

    @Transactional
    fun linkKeycloak(
        userId: Int,
        keycloakId: UUID,
        clearPassword: Boolean = true,
    ): User =
        userRepository.findUser(userId, UserSearchCriteria())
            ?.linkKeycloak(keycloakId)
            ?.run { if (clearPassword) clearPassword() else this }
            ?.let { userRepository.save(it) }
            ?: throw BusinessException(UserErrorCode.NOT_FOUND)

    fun findUser(
        userId: Int,
        criteria: UserSearchCriteria,
    ): User =
        userRepository.findUser(userId, criteria)
            ?: throw BusinessException(UserErrorCode.NOT_FOUND)

    fun findUser(
        criteria: UserSearchCriteria,
    ): User =
        userRepository.findUser(criteria)
            ?: throw BusinessException(UserErrorCode.NOT_FOUND)

    fun findUserWithProfile(
        userId: Int,
        criteria: UserSearchCriteria,
    ): UserProfileProjection =
        userRepository.findUserWithProfile(userId, criteria)
            ?: throw BusinessException(UserErrorCode.NOT_FOUND)

    fun findUserWithProfile(
        criteria: UserSearchCriteria,
    ): UserProfileProjection =
        userRepository.findUserWithProfile(criteria)
            ?: throw BusinessException(UserErrorCode.NOT_FOUND)

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
    ): List<UserProfileProjection> =
        userRepository.findUsersWithProfile(criteria)

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserProfileProjection> =
        userRepository.findUsersWithProfile(criteria, pageable)
}