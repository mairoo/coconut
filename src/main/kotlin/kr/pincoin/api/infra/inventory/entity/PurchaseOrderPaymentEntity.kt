package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_purchaseorderpayment")
class PurchaseOrderPaymentEntity private constructor(
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

    @Column(name = "order_id")
    val orderId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            account: Int,
            amount: BigDecimal,
            orderId: Long,
            isRemoved: Boolean = false,
        ) = PurchaseOrderPaymentEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            account = account,
            amount = amount,
            orderId = orderId,
        )
    }
}