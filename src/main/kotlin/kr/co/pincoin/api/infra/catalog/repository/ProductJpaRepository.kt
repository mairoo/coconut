package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.infra.catalog.entity.ProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ProductJpaRepository : JpaRepository<ProductEntity, Long> {
}