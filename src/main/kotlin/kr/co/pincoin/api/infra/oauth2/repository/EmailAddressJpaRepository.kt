package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.infra.oauth2.entity.EmailAddressEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmailAddressJpaRepository : JpaRepository<EmailAddressEntity, Long> {
}