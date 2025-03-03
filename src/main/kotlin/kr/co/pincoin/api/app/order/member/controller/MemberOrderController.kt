package kr.co.pincoin.api.app.order.member.controller

import kr.co.pincoin.api.app.order.member.request.OrderSearchRequest
import kr.co.pincoin.api.app.order.member.response.OrderResponse
import kr.co.pincoin.api.app.order.member.service.MemberOrderService
import kr.co.pincoin.api.global.response.success.ApiResponse
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
        request: OrderSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<OrderResponse>>> =
        memberOrderService.getOrders(request, pageable)
            .map { OrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getOrder(
        @PathVariable id: Long,
        request: OrderSearchRequest,
    ): ResponseEntity<ApiResponse<OrderResponse>> =
        memberOrderService.getOrder(id, request)
            .let { OrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}