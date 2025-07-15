package kr.pincoin.api.app.user.my.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.my.request.MyPasswordChangeRequest
import kr.pincoin.api.app.user.my.response.MyUserResponse
import kr.pincoin.api.app.user.my.service.MyUserService
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.security.annotation.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my")
class MyUserController(
    private val myUserService: MyUserService,
) {
    @PatchMapping("/password")
    fun updatePassword(
        @CurrentUser user: User,
        @Valid @RequestBody request: MyPasswordChangeRequest,
    ): ResponseEntity<ApiResponse<MyUserResponse>> =
        myUserService.updatePassword(
            userId = checkNotNull(user.id) { "인증사용자이므로 반드시 ID 존재" },
            request,
        )
            .let { MyUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}