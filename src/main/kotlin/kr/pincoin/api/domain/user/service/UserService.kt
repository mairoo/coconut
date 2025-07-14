package kr.pincoin.api.domain.user.service

import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.app.auth.request.UserCreateRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
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

    @Transactional
    fun createUser(request: UserCreateRequest): User {
        try {
            return userRepository.save(
                User.of(
                    username = request.username,
                    email = request.email,
                    password = passwordEncoder.encode(request.password),
                    firstName = request.firstName,
                    lastName = request.lastName,
                    isStaff = false,
                    isSuperuser = false,
                    isActive = true,
                )
            )
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(UserErrorCode.ALREADY_EXISTS)
        }
    }

    @Transactional
    fun createUser(request: AdminUserCreateRequest): User {
        try {
            return userRepository.save(
                User.of(
                    username = request.username,
                    email = request.email,
                    password = passwordEncoder.encode(request.password),
                    firstName = request.firstName,
                    lastName = request.lastName,
                    isStaff = request.isStaff,
                    isSuperuser = request.isSuperuser,
                    isActive = request.isActive,
                )
            )
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(UserErrorCode.ALREADY_EXISTS)
        }
    }
}