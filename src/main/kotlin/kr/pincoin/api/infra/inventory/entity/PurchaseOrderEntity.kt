package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_purchaseorder")
class PurchaseOrderEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "title")
    val title: String,

    @Column(name = "content")
    val content: String,

    @Column(name = "bank_account")
    val bankAccount: String?,

    @Column(name = "amount")
    val amount: BigDecimal,

    @Column(name = "paid")
    val paid: Boolean,
) {
    companion object {
        fun of(
            id: Long? = null,
            title: String,
            content: String = "",
            bankAccount: String? = null,
            amount: BigDecimal,
            paid: Boolean = false,
            isRemoved: Boolean = false,
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