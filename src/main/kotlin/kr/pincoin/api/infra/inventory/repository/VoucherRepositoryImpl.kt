package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Voucher
import kr.pincoin.api.domain.inventory.repository.VoucherRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class VoucherRepositoryImpl(
    private val jpaRepository: VoucherJpaRepository,
    private val queryRepository: VoucherQueryRepository,
) : VoucherRepository {
    override fun save(
        voucher: Voucher,
    ): Voucher =
        voucher.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("상품권 저장 실패")
}