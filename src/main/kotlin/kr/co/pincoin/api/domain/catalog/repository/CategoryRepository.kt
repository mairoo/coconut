package kr.co.pincoin.api.domain.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Category

interface CategoryRepository {
    fun save(
        category: Category,
    ): Category
}