package kr.pincoin.api.app.user.member.service

import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import org.springframework.stereotype.Service

@Service
class MemberUserService(
    private val userResourceCoordinator: UserResourceCoordinator,
) {
}