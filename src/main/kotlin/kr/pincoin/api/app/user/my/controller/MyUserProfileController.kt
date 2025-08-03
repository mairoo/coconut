package kr.pincoin.api.app.user.my.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.my.request.MyPasswordChangeRequest
import kr.pincoin.api.app.user.my.service.MyUserProfileService
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.security.extension.getCurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my")
class MyUserProfileController(
    private val myUserProfileService: MyUserProfileService,
) {
    @PatchMapping("/password")
    fun changeUserPassword(
        @Valid @RequestBody request: MyPasswordChangeRequest,
        authentication: Authentication,
    ): ResponseEntity<ApiResponse<Nothing?>> =
        myUserProfileService.changeUserPassword(
            authentication.getCurrentUser(),
            request,
        )
            .let {
                ApiResponse.of(
                    data = null,
                    message = "사용자 비밀번호가 성공적으로 변경되었습니다.",
                )
            }
            .let { ResponseEntity.ok(it) }
}