package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.infra.message.entity.FaqMessageEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface FaqMessageJpaRepository : JpaRepository<FaqMessageEntity, Long>