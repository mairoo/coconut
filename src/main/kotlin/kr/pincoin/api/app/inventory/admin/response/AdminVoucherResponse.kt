package kr.pincoin.api.app.inventory.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.pincoin.api.domain.inventory.model.Voucher
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AdminVoucherResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("code")
    val code: String,

    @JsonProperty("remarks")
    val remarks: String,

    @JsonProperty("status")
    val status: VoucherStatus,

    @JsonProperty("productId")
    val productId: Long,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean,

    @JsonProperty("created")
    val created: LocalDateTime?,

    @JsonProperty("modified")
    val modified: LocalDateTime?,
) {
    companion object {
        fun from(voucher: Voucher) = with(voucher) {
            AdminVoucherResponse(
                id = id ?: throw IllegalStateException("바우처 ID는 필수 입력값입니다"),
                code = code,
                remarks = remarks,
                status = status,
                productId = productId,
                isRemoved = isRemoved,
                created = created,
                modified = modified,
            )
        }
    }
}