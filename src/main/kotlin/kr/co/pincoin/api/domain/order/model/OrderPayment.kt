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
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val orderId: Long,
    val amount: BigDecimal,
    val received: ZonedDateTime,

    // 4. 도메인 로직 가변 필드
    account: PaymentBankAccount,
    balance: BigDecimal,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var account: PaymentBankAccount = account
        private set

    var balance: BigDecimal = balance
        private set

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
                isRemoved = isRemoved,
                orderId = orderId,
                account = account,
                amount = amount,
                balance = balance,
                received = received,
            )
    }
}