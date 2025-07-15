package kr.pincoin.api.infra.inventory.repository.criteria

import kr.pincoin.api.domain.inventory.enums.VoucherStatus

data class VoucherSearchCriteria(
    val voucherId: Long? = null,
    val code: String? = null,
    val remarks: String? = null,
    val status: VoucherStatus? = null,
    val productId: Long? = null,
    val isRemoved: Boolean? = null,
)