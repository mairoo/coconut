package kr.pincoin.api.domain.order.model

import java.time.LocalDateTime

class OrderProductVoucher private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val code: String,
    val revoked: Boolean = false,
    val remarks: String = "",
    val orderProductId: Long,
    val voucherId: Long? = null,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            code: String,
            revoked: Boolean = false,
            remarks: String = "",
            orderProductId: Long,
            voucherId: Long? = null,
        ): OrderProductVoucher = OrderProductVoucher(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            code = code,
            revoked = revoked,
            remarks = remarks,
            orderProductId = orderProductId,
            voucherId = voucherId,
        )
    }
}