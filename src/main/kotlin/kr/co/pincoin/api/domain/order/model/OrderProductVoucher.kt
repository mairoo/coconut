package kr.co.pincoin.api.domain.order.model

import java.time.ZonedDateTime

class OrderProductVoucher private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val orderProductId: Long,
    val code: String,

    // 4. 도메인 로직 가변 필드
    val voucherId: Long?,
    val revoked: Boolean,
    val remarks: String,
) {
    fun updateRevoked(newRevoked: Boolean? = null): OrderProductVoucher =
        copy(revoked = newRevoked ?: revoked)

    fun updateRemarks(newRemarks: String? = null): OrderProductVoucher =
        copy(remarks = newRemarks ?: remarks)

    fun markAsRemoved(): OrderProductVoucher =
        copy(isRemoved = true)

    private fun copy(
        voucherId: Long? = this.voucherId,
        revoked: Boolean? = null,
        remarks: String? = null,
        isRemoved: Boolean? = null
    ): OrderProductVoucher = OrderProductVoucher(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        orderProductId = this.orderProductId,
        code = this.code,
        voucherId = voucherId,
        revoked = revoked ?: this.revoked,
        remarks = remarks ?: this.remarks
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderProductId: Long,
            voucherId: Long? = null,
            code: String,
            revoked: Boolean = false,
            remarks: String = "",
        ): OrderProductVoucher =
            OrderProductVoucher(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                orderProductId = orderProductId,
                voucherId = voucherId,
                code = code,
                revoked = revoked,
                remarks = remarks,
            )
    }
}