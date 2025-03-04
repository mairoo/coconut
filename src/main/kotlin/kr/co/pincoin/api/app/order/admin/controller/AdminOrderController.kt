package kr.co.pincoin.api.app.order.admin.controller

import kr.co.pincoin.api.app.order.admin.request.OrderSearchRequest
import kr.co.pincoin.api.app.order.admin.response.OrderPaymentResponse
import kr.co.pincoin.api.app.order.admin.response.OrderProductResponse
import kr.co.pincoin.api.app.order.admin.response.OrderProductVoucherResponse
import kr.co.pincoin.api.app.order.admin.response.OrderUserProfileResponse
import kr.co.pincoin.api.app.order.admin.service.AdminOrderService
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Page
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
    @GetMapping
    fun searchOrders(
        request: OrderSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<OrderUserProfileResponse>>> =
        adminOrderService.getOrdersWithUserProfile(request, pageable)
            .map { OrderUserProfileResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}")
    fun getOrder(
        @PathVariable id: Long,
        request: OrderSearchRequest,
    ): ResponseEntity<ApiResponse<OrderUserProfileResponse>> =
        adminOrderService.getOrderWithUserProfile(id, request)
            .let { OrderUserProfileResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}/payments")
    fun getOrderPayments(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<List<OrderPaymentResponse>>> =
        adminOrderService.getOrderPayments(id)
            .map { OrderPaymentResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}/items")
    fun getOrderItems(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<List<OrderProductResponse>>> =
        adminOrderService.getOrderItems(id)
            .map { OrderProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{id}/vouchers")
    fun getOrderVouchers(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<List<OrderProductVoucherResponse>>> =
        adminOrderService.getOrderVouchers(id)
            .map { OrderProductVoucherResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}