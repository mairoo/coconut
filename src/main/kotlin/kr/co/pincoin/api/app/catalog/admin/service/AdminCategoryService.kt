package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.domain.catalog.service.CategoryService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminCategoryService(
    private val categoryService: CategoryService,
) {
}