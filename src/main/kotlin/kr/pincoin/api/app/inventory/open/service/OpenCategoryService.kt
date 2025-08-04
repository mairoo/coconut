package kr.pincoin.api.app.inventory.open.service

import kr.pincoin.api.domain.inventory.service.CategoryService
import org.springframework.stereotype.Service

@Service
class OpenCategoryService(
    private val categoryService: CategoryService,
) {
}