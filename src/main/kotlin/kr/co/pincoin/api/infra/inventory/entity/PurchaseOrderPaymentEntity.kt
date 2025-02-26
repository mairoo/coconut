package kr.co.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.order.converter.PaymentBankAccountConverter
import java.math.BigDecimal

@Entity
@Table(name = "shop_purchaseorderpayment")
class PurchaseOrderPaymentEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "order_id")
    val orderId: Long,

    @Column(name = "account")
    @Convert(converter = PaymentBankAccountConverter::class)
    val account: PaymentBankAccount,

    @Column(name = "amount", precision = 11, scale = 2)
    val amount: BigDecimal,

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
            account: PaymentBankAccount,
            amount: BigDecimal,
        ) = PurchaseOrderPaymentEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            orderId = orderId,
            account = account,
            amount = amount,
        )
    }
}