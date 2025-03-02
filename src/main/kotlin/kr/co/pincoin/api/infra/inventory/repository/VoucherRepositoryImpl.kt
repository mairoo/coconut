package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.Voucher
import kr.co.pincoin.api.domain.inventory.repository.VoucherRepository
import kr.co.pincoin.api.infra.inventory.mapper.toEntity
import kr.co.pincoin.api.infra.inventory.mapper.toModel
import kr.co.pincoin.api.infra.inventory.mapper.toModelList
import kr.co.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
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

    override fun saveAll(
        vouchers: List<Voucher>,
    ): List<Voucher> {
        if (vouchers.isEmpty()) return emptyList()

        // 기존 상품권(id가 있는)과 새 상품권을 분리
        val (existingVouchers, newVouchers) = vouchers.partition { it.id != null }

        // 기존 상품권 목록 UPDATE
        if (existingVouchers.isNotEmpty()) {
            jdbcRepository.batchUpdate(existingVouchers)
        }

        // 새 상품권은 INSERT
        if (newVouchers.isNotEmpty()) {
            jdbcRepository.batchInsert(newVouchers)
        }

        return vouchers
    }

    override fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): Voucher? =
        queryRepository.findVoucher(criteria)?.toModel()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
    ): List<Voucher> =
        queryRepository.findVouchers(criteria).toModelList()

    override fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher> =
        queryRepository.findVouchers(criteria, pageable).map { it.toModel() }
}