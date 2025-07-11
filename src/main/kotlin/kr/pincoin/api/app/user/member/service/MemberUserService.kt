package kr.pincoin.api.app.user.member.service

import kr.pincoin.api.app.user.member.request.MemberUserCreateRequest
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.model.User
import org.springframework.stereotype.Service

@Service
class MemberUserService(
    private val userResourceCoordinator: UserResourceCoordinator,
) {
    fun createUser(
        request: MemberUserCreateRequest,
    ): User =
        userResourceCoordinator.createUser(request)
}