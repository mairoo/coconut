package kr.pincoin.api.app.inventory.open.service

import kr.pincoin.api.domain.inventory.service.ProductService
import org.springframework.stereotype.Service

@Service
class OpenProductService(
    private val productService: ProductService,
) {
}