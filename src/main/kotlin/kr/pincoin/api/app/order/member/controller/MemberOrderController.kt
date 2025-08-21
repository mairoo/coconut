package kr.pincoin.api.app.order.member.controller

import kr.pincoin.api.app.order.member.service.MemberOrderService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/member/orders")
class MemberOrderController(
    private val memberOrderService: MemberOrderService,
) {
    // 주문 생성
}