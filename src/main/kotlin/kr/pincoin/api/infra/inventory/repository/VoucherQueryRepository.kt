package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.VoucherEntity
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VoucherQueryRepository {
    fun findById(
        id: Long,
    ): VoucherEntity?

    fun findVoucher(
        voucherId: Long,
        criteria: VoucherSearchCriteria,
    ): VoucherEntity?

    fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): VoucherEntity?

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<VoucherEntity>
}