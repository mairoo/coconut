package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.domain.catalog.service.ProductService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminProductService(
    private val productService: ProductService,
) {
}