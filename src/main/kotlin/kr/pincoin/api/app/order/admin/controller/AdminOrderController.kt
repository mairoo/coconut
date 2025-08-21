package kr.pincoin.api.app.order.admin.controller

import kr.pincoin.api.app.order.admin.service.AdminOrderService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/categories")
class AdminOrderController(
    private val adminOrderService: AdminOrderService,
) {
}