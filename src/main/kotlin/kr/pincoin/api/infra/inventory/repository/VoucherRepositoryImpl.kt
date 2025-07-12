package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.repository.VoucherRepository
import org.springframework.stereotype.Repository

@Repository
class VoucherRepositoryImpl(
    private val jpaRepository: VoucherJpaRepository,
    private val queryRepository: VoucherQueryRepository,
) : VoucherRepository {
}