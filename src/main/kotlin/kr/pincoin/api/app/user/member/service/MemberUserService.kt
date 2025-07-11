package kr.pincoin.api.app.user.member.service

import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated()")
class MemberUserService(
    private val userResourceCoordinator: UserResourceCoordinator,
) {
}