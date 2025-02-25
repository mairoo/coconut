package kr.co.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.order.converter.PaymentBankAccountConverter
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "shop_orderpayment")
class OrderPaymentEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "order_id")
    val orderId: Long,

    @Column(name = "account")
    @Convert(converter = PaymentBankAccountConverter::class)
    val account: PaymentBankAccount = PaymentBankAccount.KB,

    @Column(name = "amount", precision = 11, scale = 2)
    val amount: BigDecimal,

    @Column(name = "balance", precision = 11, scale = 2)
    val balance: BigDecimal = BigDecimal.ZERO,

    @Column(name = "received")
    val received: LocalDateTime,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
            orderId: Long,
            account: PaymentBankAccount = PaymentBankAccount.KB,
            amount: BigDecimal,
            balance: BigDecimal = BigDecimal.ZERO,
            received: LocalDateTime,
        ) = OrderPaymentEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            orderId = orderId,
            account = account,
            amount = amount,
            balance = balance,
            received = received,
        )
    }
}