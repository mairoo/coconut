package kr.pincoin.api.app.user.admin.service

import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.model.User
import org.springframework.stereotype.Service

@Service
class AdminUserService(
    private val userResourceCoordinator: UserResourceCoordinator,
) {
    fun createUser(
        request: AdminUserCreateRequest,
    ): User =
        userResourceCoordinator.createUser(request)
}