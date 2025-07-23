package kr.pincoin.api.domain.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

class OrderMileageLog private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val mileage: BigDecimal,
    val orderId: Long? = null,
    val userId: Int,
    val memo: String,
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        mileage: BigDecimal = this.mileage,
        orderId: Long? = this.orderId,
        userId: Int = this.userId,
        memo: String = this.memo,
    ): OrderMileageLog = OrderMileageLog(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        mileage = mileage,
        orderId = orderId,
        userId = userId,
        memo = memo,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            mileage: BigDecimal,
            orderId: Long? = null,
            userId: Int,
            memo: String,
        ): OrderMileageLog = OrderMileageLog(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            mileage = mileage,
            orderId = orderId,
            userId = userId,
            memo = memo,
        )
    }
}