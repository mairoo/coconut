package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Voucher

interface VoucherRepository {
    fun save(
        voucher: Voucher,
    ): Voucher
}