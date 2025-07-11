package kr.pincoin.api.domain.coordinator.user

import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.app.user.member.request.MemberUserCreateRequest
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.ProfileService
import kr.pincoin.api.domain.user.service.UserService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserResourceCoordinator(
    private val userService: UserService,
    private val profileService: ProfileService,
) {
    @Transactional
    fun createUser(
        request: MemberUserCreateRequest,
    ): User {
        // 1. User 엔티티 생성
        val user = userService.createUser(request)

        // 2. User ID를 사용하여 빈 Profile 엔티티 생성
        user.id?.let { userId ->
            profileService.createProfile(userId)
        }

        return user
    }

    @Transactional
    fun createUser(
        request: AdminUserCreateRequest,
    ): User {
        // 1. User 엔티티 생성
        val user = userService.createUser(request)

        // 2. User ID를 사용하여 빈 Profile 엔티티 생성
        user.id?.let { userId ->
            profileService.createProfile(userId)
        }

        return user
    }
}