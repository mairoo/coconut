package kr.pincoin.api.app.order.my.controller

import kr.pincoin.api.app.order.my.request.MyOrderSearchRequest
import kr.pincoin.api.app.order.my.response.MyOrderResponse
import kr.pincoin.api.app.order.my.service.MyOrderService
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.global.response.page.PageResponse
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.security.annotation.CurrentUser
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/my/orders")
class MyOrderController(
    private val myOrderService: MyOrderService,
) {
    /**
     * 나의 주문 상세보기
     */
    @GetMapping("/{orderId}")
    fun getMyOrder(
        @PathVariable orderId: Long,
        @CurrentUser user: User,
        request: MyOrderSearchRequest,
    ): ResponseEntity<ApiResponse<MyOrderResponse>> =
        myOrderService.getOrder(
            orderId,
            userId = checkNotNull(user.id) { "인증사용자이므로 반드시 ID 존재" },
            request,
        )
            .let { MyOrderResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 나의 주문 목록
     */
    @GetMapping
    fun getOrders(
        request: MyOrderSearchRequest,
        @CurrentUser user: User,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<PageResponse<MyOrderResponse>>> =
        myOrderService.getOrders(
            request,
            userId = checkNotNull(user.id) { "인증사용자이므로 반드시 ID 존재" },
            pageable,
        )
            .map { MyOrderResponse.from(it) }
            .let { PageResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 나의 주문 환불 요청
     */
}