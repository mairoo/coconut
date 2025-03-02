package kr.co.pincoin.api.infra.inventory.repository.criteria

import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus

data class VoucherSearchCriteria(
    val id: Long? = null,
    val code: String? = null,
    val remarks: String? = null,
    val status: VoucherStatus? = null,
    val isRemoved: Boolean? = null,
    val productId: Long? = null,
)
