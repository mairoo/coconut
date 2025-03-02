package kr.co.pincoin.api.domain.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.Voucher
import kr.co.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface VoucherRepository {
    fun save(
        voucher: Voucher,
    ): Voucher

    fun saveAll(
        vouchers: List<Voucher>,
    ): List<Voucher>

    fun findVoucher(
        criteria: VoucherSearchCriteria,
    ): Voucher?

    fun findVouchers(
        criteria: VoucherSearchCriteria,
    ): List<Voucher>

    fun findVouchers(
        criteria: VoucherSearchCriteria,
        pageable: Pageable,
    ): Page<Voucher>
}