package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.infra.inventory.entity.VoucherEntity
import kr.co.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VoucherQueryRepository {
    fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): VoucherEntity?

    fun findVouchers(
        criteria: VoucherSearchCriteria,
    ): List<VoucherEntity>

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<VoucherEntity>
}