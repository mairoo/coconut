package kr.pincoin.api.app.user.member.controller

import kr.pincoin.api.app.user.member.service.MemberUserService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/users")
class MemberUserController(
    private val memberUserService: MemberUserService,
) {
}