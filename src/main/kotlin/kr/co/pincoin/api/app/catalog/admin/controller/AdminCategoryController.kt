package kr.co.pincoin.api.app.catalog.admin.controller

import kr.co.pincoin.api.app.catalog.admin.service.AdminCategoryService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/categories")
class AdminCategoryController(
    private val adminCategoryService: AdminCategoryService,
) {
}