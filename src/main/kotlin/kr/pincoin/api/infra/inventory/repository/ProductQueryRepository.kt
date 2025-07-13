package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.ProductEntity

interface ProductQueryRepository {
    fun findById(
        id: Long,
    ): ProductEntity?
}