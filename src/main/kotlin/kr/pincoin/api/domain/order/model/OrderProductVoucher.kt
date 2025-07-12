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
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        code: String = this.code,
        revoked: Boolean = this.revoked,
        remarks: String = this.remarks,
        orderProductId: Long = this.orderProductId,
        voucherId: Long? = this.voucherId,
    ): OrderProductVoucher = OrderProductVoucher(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        code = code,
        revoked = revoked,
        remarks = remarks,
        orderProductId = orderProductId,
        voucherId = voucherId,
    )

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