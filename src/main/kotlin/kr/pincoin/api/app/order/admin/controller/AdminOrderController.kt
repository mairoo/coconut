package kr.pincoin.api.app.order.admin.controller

import kr.pincoin.api.app.order.admin.request.AdminOrderSearchRequest
import kr.pincoin.api.app.order.admin.response.AdminOrderResponse
import kr.pincoin.api.app.order.admin.service.AdminOrderService
import kr.pincoin.api.global.response.page.PageResponse
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/orders")
class AdminOrderController(
    private val adminOrderService: AdminOrderService,
) {
    /**
     * 주문 상세보기
     */
    @GetMapping("/{orderId}")
    fun getOrder(
        @PathVariable orderId: Long,
        request: AdminOrderSearchRequest,
    ): ResponseEntity<ApiResponse<AdminOrderResponse>> =
        adminOrderService.getOrder(orderId, request)
            .let { AdminOrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 주문 목록
     */
    @GetMapping
    fun getOrders(
        request: AdminOrderSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<PageResponse<AdminOrderResponse>>> =
        adminOrderService.getOrders(request, pageable)
            .map { AdminOrderResponse.from(it) }
            .let { PageResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 환불 처리: 승인 또는 반려
     */
}