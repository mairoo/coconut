package kr.co.pincoin.api.domain.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.Voucher

interface VoucherRepository {
    fun save(
        voucher: Voucher,
    ): Voucher

    fun saveAll(
        vouchers: List<Voucher>,
    ): List<Voucher>
}