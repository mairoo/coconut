package kr.co.pincoin.api.domain.order.model

import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import java.math.BigDecimal
import java.time.ZonedDateTime

class OrderPayment private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val orderId: Long,
    val amount: BigDecimal,
    val received: ZonedDateTime,

    // 4. 도메인 로직 가변 필드
    val account: PaymentBankAccount,
    val balance: BigDecimal,
) {
    fun updateAccount(newAccount: PaymentBankAccount? = null): OrderPayment =
        copy(account = newAccount ?: account)

    fun updateBalance(newBalance: BigDecimal? = null): OrderPayment =
        copy(balance = newBalance ?: balance)

    fun markAsRemoved(): OrderPayment =
        copy(isRemoved = true)

    private fun copy(
        account: PaymentBankAccount? = null,
        balance: BigDecimal? = null,
        isRemoved: Boolean? = null
    ): OrderPayment = OrderPayment(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        orderId = this.orderId,
        amount = this.amount,
        received = this.received,
        account = account ?: this.account,
        balance = balance ?: this.balance
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderId: Long,
            account: PaymentBankAccount = PaymentBankAccount.KB,
            amount: BigDecimal,
            balance: BigDecimal = BigDecimal.ZERO,
            received: ZonedDateTime,
        ): OrderPayment =
            OrderPayment(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                orderId = orderId,
                account = account,
                amount = amount,
                balance = balance,
                received = received,
            )
    }
}