package kr.pincoin.api.app.order.member.controller

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.Valid
import kr.pincoin.api.app.order.member.request.MemberOrderCreateRequest
import kr.pincoin.api.app.order.member.service.MemberOrderService
import kr.pincoin.api.global.response.success.ApiResponse
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
        @Valid @RequestBody request: MemberOrderCreateRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        logger.info { "Received order creation request: $request" }

        return ResponseEntity.ok(ApiResponse.of(Unit))
    }
}