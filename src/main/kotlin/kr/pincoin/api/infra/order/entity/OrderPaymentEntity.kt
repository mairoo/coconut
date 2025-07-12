package kr.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "shop_orderpayment")
class OrderPaymentEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "account")
    val account: Int,

    @Column(name = "amount")
    val amount: BigDecimal,

    @Column(name = "received")
    val received: LocalDateTime,

    @Column(name = "order_id")
    val orderId: Long,

    @Column(name = "balance")
    val balance: BigDecimal,
) {
    companion object {
        fun of(
            id: Long? = null,
            account: Int,
            amount: BigDecimal,
            received: LocalDateTime = LocalDateTime.now(),
            orderId: Long,
            balance: BigDecimal = BigDecimal.ZERO,
            isRemoved: Boolean = false,
        ) = OrderPaymentEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            account = account,
            amount = amount,
            received = received,
            orderId = orderId,
            balance = balance,
        )
    }
}