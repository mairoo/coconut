package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.TotpDeviceEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TotpDeviceJpaRepository : JpaRepository<TotpDeviceEntity, Int> {
}