package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.VoucherEntity

interface VoucherQueryRepository {
    fun findById(
        id: Long,
    ): VoucherEntity?
}