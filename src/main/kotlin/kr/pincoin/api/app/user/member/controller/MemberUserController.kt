package kr.pincoin.api.app.user.member.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.member.request.MemberUserCreateRequest
import kr.pincoin.api.app.user.member.response.MemberUserResponse
import kr.pincoin.api.app.user.member.service.MemberUserService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class MemberUserController(
    private val memberUserService: MemberUserService,
) {
    @PostMapping
    fun createUser(
        @Valid @RequestBody request: MemberUserCreateRequest,
    ): ResponseEntity<ApiResponse<MemberUserResponse>> =
        memberUserService.createUser(request)
            .let { MemberUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}