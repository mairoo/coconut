package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Category

interface CategoryRepository {
    fun save(
        category: Category,
    ): Category

    fun findById(
        categoryId: Long,
    ): Category?
}