package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.EmailBannedEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailBannedJpaRepository : JpaRepository<EmailBannedEntity, Long> {
}