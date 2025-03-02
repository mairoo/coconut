package kr.co.pincoin.api.domain.inventory.model

import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import java.math.BigDecimal
import java.time.ZonedDateTime

class PurchaseOrderPayment private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val orderId: Long,

    // 4. 도메인 로직 가변 필드
    val account: PaymentBankAccount,
    val amount: BigDecimal,
) {
    fun markAsRemoved(): PurchaseOrderPayment =
        copy(isRemoved = true)

    private fun copy(
        account: PaymentBankAccount? = null,
        amount: BigDecimal? = null,
        isRemoved: Boolean? = null
    ): PurchaseOrderPayment = PurchaseOrderPayment(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        orderId = this.orderId,
        account = account ?: this.account,
        amount = amount ?: this.amount
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderId: Long,
            account: PaymentBankAccount,
            amount: BigDecimal,
        ): PurchaseOrderPayment =
            PurchaseOrderPayment(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                orderId = orderId,
                account = account,
                amount = amount,
            )
    }
}