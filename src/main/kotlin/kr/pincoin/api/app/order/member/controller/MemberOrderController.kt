package kr.pincoin.api.app.order.member.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.order.member.request.MemberOrderCreateRequest
import kr.pincoin.api.app.order.member.response.MemberOrderResponse
import kr.pincoin.api.app.order.member.service.MemberOrderService
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.security.annotation.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/orders")
class MemberOrderController(
    private val memberOrderService: MemberOrderService,
) {
    private val logger = KotlinLogging.logger {}

    @PostMapping
    fun createOrder(
        @CurrentUser user: User,
        @Valid @RequestBody request: MemberOrderCreateRequest,
        httpRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<MemberOrderResponse>> {
        val order = memberOrderService.createOrder(user.id!!, request, httpRequest)
        val response = MemberOrderResponse.from(order)

        return ResponseEntity.ok(ApiResponse.of(response))
    }
}