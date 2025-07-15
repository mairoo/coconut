package kr.pincoin.api.app.user.admin.service

import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminUserService(
    private val userService: UserService,
) {
    fun findUser(
        userId: Int,
        criteria: UserSearchCriteria = UserSearchCriteria(),
    ): User =
        userService.findUser(userId, criteria)

    fun findUser(
        criteria: UserSearchCriteria,
    ): User =
        userService.findUser(criteria)

    fun findUserWithProfile(
        userId: Int,
        criteria: UserSearchCriteria = UserSearchCriteria(),
    ): UserProfileProjection =
        userService.findUserWithProfile(userId, criteria)

    fun findUserWithProfile(
        criteria: UserSearchCriteria,
    ): UserProfileProjection =
        userService.findUserWithProfile(criteria)

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
    ): List<UserProfileProjection> =
        userService.findUsersWithProfile(criteria)

    fun findUsersWithProfile(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserProfileProjection> =
        userService.findUsersWithProfile(criteria, pageable)

    fun createUser(
        request: AdminUserCreateRequest,
    ): User =
        userService.createUser(request)

    fun updateUserStatus(
        userId: Int,
        isActive: Boolean,
    ): User =
        userService.updateUserStatus(userId, isActive)

    fun softDeleteUser(
        userId: Int,
    ): User =
        userService.softDeleteUser(userId)
}