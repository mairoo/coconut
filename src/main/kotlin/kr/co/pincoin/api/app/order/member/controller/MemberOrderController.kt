package kr.co.pincoin.api.app.order.member.controller

import kr.co.pincoin.api.app.order.member.request.OrderSearchRequest
import kr.co.pincoin.api.app.order.member.response.OrderResponse
import kr.co.pincoin.api.app.order.member.service.MemberOrderService
import kr.co.pincoin.api.domain.user.model.User
import kr.co.pincoin.api.global.response.success.ApiResponse
import kr.co.pincoin.api.global.security.annotation.auth.CurrentUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/orders")
class MemberOrderController(
    private val memberOrderService: MemberOrderService,
) {
    @GetMapping
    fun searchOrders(
        @CurrentUser user: User,
        request: OrderSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<OrderResponse>>> =
        memberOrderService.getOrders(
            userId = checkNotNull(user.id) { "인증사용자이므로 반드시 ID 존재" },
            request,
            pageable,
        )
            .map { OrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getOrder(
        @PathVariable id: Long,
        @CurrentUser user: User,
        request: OrderSearchRequest,
    ): ResponseEntity<ApiResponse<OrderResponse>> =
        memberOrderService.getOrder(
            userId = checkNotNull(user.id) { "인증사용자이므로 반드시 ID 존재" },
            id,
            request,
        )
            .let { OrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}