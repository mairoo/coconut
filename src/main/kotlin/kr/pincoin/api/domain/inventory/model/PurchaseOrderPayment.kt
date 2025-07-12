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
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        account: Int = this.account,
        amount: BigDecimal = this.amount,
        orderId: Long = this.orderId,
    ): PurchaseOrderPayment = PurchaseOrderPayment(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        account = account,
        amount = amount,
        orderId = orderId,
    )

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