package kr.co.pincoin.api.app.catalog.admin.controller

import kr.co.pincoin.api.app.catalog.admin.service.AdminProductService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/products")
class AdminProductController(
    private val adminProductService: AdminProductService,
) {
}