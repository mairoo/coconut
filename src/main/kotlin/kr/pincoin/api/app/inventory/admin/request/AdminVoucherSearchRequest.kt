package kr.pincoin.api.app.inventory.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.pincoin.api.infra.inventory.repository.criteria.VoucherSearchCriteria

data class AdminVoucherSearchRequest(
    @JsonProperty("voucherId")
    val voucherId: Long? = null,

    @JsonProperty("code")
    val code: String? = null,

    @JsonProperty("remarks")
    val remarks: String? = null,

    @JsonProperty("status")
    val status: VoucherStatus? = null,

    @JsonProperty("productId")
    val productId: Long? = null,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean? = false,
) {
    fun toSearchCriteria() = VoucherSearchCriteria(
        voucherId = voucherId,
        code = code,
        remarks = remarks,
        status = status,
        productId = productId,
        isRemoved = isRemoved,
    )
}