package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Voucher
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VoucherRepository {
    fun save(
        voucher: Voucher,
    ): Voucher

    fun findById(
        id: Long,
    ): Voucher?

    fun findVoucher(
        voucherId: Long,
        criteria: VoucherSearchCriteria,
    ): Voucher?

    fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): Voucher?

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher>
}