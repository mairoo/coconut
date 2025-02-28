package kr.co.pincoin.api.app.catalog.member.service

import kr.co.pincoin.api.domain.catalog.service.ProductService
import org.springframework.stereotype.Service

@Service
class MemberProductService(
    private val productService: ProductService,
) {
}