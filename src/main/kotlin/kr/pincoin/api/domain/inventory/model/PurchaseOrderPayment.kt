package kr.pincoin.api.domain.inventory.model

import java.math.BigDecimal
import java.time.LocalDateTime

class PurchaseOrderPayment private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val account: Int,
    val amount: BigDecimal,
    val orderId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            account: Int,
            amount: BigDecimal,
            orderId: Long,
        ): PurchaseOrderPayment = PurchaseOrderPayment(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            account = account,
            amount = amount,
            orderId = orderId,
        )
    }
}