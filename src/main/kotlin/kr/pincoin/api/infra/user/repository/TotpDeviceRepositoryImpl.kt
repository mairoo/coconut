package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.repository.TotpDeviceRepository
import org.springframework.stereotype.Repository

@Repository
class TotpDeviceRepositoryImpl(
    private val jpaRepository: TotpDeviceJpaRepository,
) : TotpDeviceRepository {
}