package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Voucher
import kr.pincoin.api.domain.inventory.repository.VoucherRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class VoucherRepositoryImpl(
    private val jpaRepository: VoucherJpaRepository,
    private val queryRepository: VoucherQueryRepository,
    private val jdbcRepository: VoucherJdbcRepository,
) : VoucherRepository {
    override fun save(
        voucher: Voucher,
    ): Voucher =
        voucher.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("상품권 저장 실패")

    override fun findById(
        id: Long,
    ): Voucher? =
        queryRepository.findById(id)?.toModel()

    override fun findVoucher(
        voucherId: Long,
        criteria: VoucherSearchCriteria
    ): Voucher? =
        queryRepository.findVoucher(voucherId, criteria)?.toModel()

    override fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): Voucher? =
        queryRepository.findVoucher(criteria)?.toModel()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher> =
        queryRepository.findVouchers(criteria, pageable)
            .map { it.toModel()!! }
}