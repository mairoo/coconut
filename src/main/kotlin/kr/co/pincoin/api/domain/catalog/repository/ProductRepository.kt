package kr.co.pincoin.api.domain.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Product

interface ProductRepository {
    fun save(
        product: Product,
    ): Product
}