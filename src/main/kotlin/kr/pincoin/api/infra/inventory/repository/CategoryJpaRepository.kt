package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
}