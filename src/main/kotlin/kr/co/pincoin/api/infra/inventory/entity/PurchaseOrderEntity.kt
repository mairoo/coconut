package kr.co.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_purchaseorder")
class PurchaseOrderEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "title")
    val title: String,

    @Column(name = "content", columnDefinition = "TEXT")
    val content: String,

    @Column(name = "bank_account")
    val bankAccount: String? = null,

    @Column(name = "amount", precision = 11, scale = 2)
    val amount: BigDecimal,

    @Column(name = "paid")
    val paid: Boolean = false,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
            title: String,
            content: String,
            bankAccount: String? = null,
            amount: BigDecimal,
            paid: Boolean = false,
        ) = PurchaseOrderEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            title = title,
            content = content,
            bankAccount = bankAccount,
            amount = amount,
            paid = paid,
        )
    }
}