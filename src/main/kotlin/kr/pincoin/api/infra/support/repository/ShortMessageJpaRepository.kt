package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.ShortMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ShortMessageJpaRepository : JpaRepository<ShortMessageEntity, Long> {
}