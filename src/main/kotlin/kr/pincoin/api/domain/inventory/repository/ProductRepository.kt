package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Product

interface ProductRepository {
    fun save(
        product: Product,
    ): Product

    fun findById(
        productId: Long,
    ): Product?
}