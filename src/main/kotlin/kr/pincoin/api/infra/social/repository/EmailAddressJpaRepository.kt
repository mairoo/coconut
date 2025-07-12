package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.EmailAddressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailAddressJpaRepository : JpaRepository<EmailAddressEntity, Int> {
}