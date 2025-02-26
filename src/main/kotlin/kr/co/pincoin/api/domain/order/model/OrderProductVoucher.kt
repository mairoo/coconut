package kr.co.pincoin.api.domain.order.model

import java.time.ZonedDateTime

class OrderProductVoucher private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val orderProductId: Long,
    val code: String,

    // 4. 도메인 로직 가변 필드
    voucherId: Long?,
    revoked: Boolean,
    remarks: String,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var voucherId: Long? = voucherId
        private set

    var revoked: Boolean = revoked
        private set

    var remarks: String = remarks
        private set

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
                isRemoved = isRemoved,
                orderProductId = orderProductId,
                voucherId = voucherId,
                code = code,
                revoked = revoked,
                remarks = remarks,
            )
    }
}