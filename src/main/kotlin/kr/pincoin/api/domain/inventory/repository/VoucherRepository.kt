package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Voucher
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria

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
}