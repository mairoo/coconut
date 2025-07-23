package kr.pincoin.api.domain.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

class OrderPayment private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val account: Int,
    val amount: BigDecimal,
    val received: LocalDateTime = LocalDateTime.now(),
    val orderId: Long,
    val balance: BigDecimal = BigDecimal.ZERO,
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        account: Int = this.account,
        amount: BigDecimal = this.amount,
        received: LocalDateTime = this.received,
        orderId: Long = this.orderId,
        balance: BigDecimal = this.balance,
    ): OrderPayment = OrderPayment(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        account = account,
        amount = amount,
        received = received,
        orderId = orderId,
        balance = balance,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            account: Int,
            amount: BigDecimal,
            received: LocalDateTime = LocalDateTime.now(),
            orderId: Long,
            balance: BigDecimal = BigDecimal.ZERO,
        ): OrderPayment = OrderPayment(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            account = account,
            amount = amount,
            received = received,
            orderId = orderId,
            balance = balance,
        )
    }
}