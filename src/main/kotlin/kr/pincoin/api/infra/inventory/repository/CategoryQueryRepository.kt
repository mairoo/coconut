package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.CategoryEntity

interface CategoryQueryRepository {
    fun findById(
        id: Long,
    ): CategoryEntity?
}