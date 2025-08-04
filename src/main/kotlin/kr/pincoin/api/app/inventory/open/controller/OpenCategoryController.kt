package kr.pincoin.api.app.inventory.open.controller

import kr.pincoin.api.app.inventory.open.service.OpenCategoryService
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenCategoryController(
    private val openCategoryService: OpenCategoryService,
) {
}