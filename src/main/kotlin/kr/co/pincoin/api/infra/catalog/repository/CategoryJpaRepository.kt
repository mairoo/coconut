package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.infra.catalog.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
}