package kr.pincoin.api.app.order.my.controller

import kr.pincoin.api.app.order.admin.response.AdminOrderResponse
import kr.pincoin.api.app.order.my.request.MyOrderSearchRequest
import kr.pincoin.api.app.order.my.service.MyOrderService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/orders")
class MyOrderController(
    private val myOrderService: MyOrderService,
) {
    /**
     * 나의 주문 상세보기
     */
    @GetMapping("/{orderId}")
    fun getOrder(
        @PathVariable orderId: Long,
        request: MyOrderSearchRequest,
    ): ResponseEntity<ApiResponse<AdminOrderResponse>> =
        myOrderService.getOrder(orderId, request)
            .let { AdminOrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 나의 주문 목록
     */

    /**
     * 나의 주문 환불 요청
     */
}