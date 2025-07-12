package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.FaqMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FaqMessageJpaRepository : JpaRepository<FaqMessageEntity, Long> {
}