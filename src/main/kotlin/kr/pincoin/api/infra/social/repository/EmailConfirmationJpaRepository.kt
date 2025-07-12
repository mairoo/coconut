package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.EmailConfirmationEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailConfirmationJpaRepository : JpaRepository<EmailConfirmationEntity, Int> {
}