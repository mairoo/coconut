package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.PhoneVerificationLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PhoneVerificationLogJpaRepository : JpaRepository<PhoneVerificationLogEntity, Long> {
}