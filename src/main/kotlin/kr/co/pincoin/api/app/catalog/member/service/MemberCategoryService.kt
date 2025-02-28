package kr.co.pincoin.api.app.catalog.member.service

import kr.co.pincoin.api.domain.catalog.service.CategoryService
import org.springframework.stereotype.Service

@Service
class MemberCategoryService(
    private val categoryService: CategoryService,
) {
}