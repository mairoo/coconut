package kr.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_orderproduct")
class OrderProductEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "name")
    val name: String,

    @Column(name = "subtitle")
    val subtitle: String,

    @Column(name = "code")
    val code: String,

    @Column(name = "list_price")
    val listPrice: BigDecimal,

    @Column(name = "selling_price")
    val sellingPrice: BigDecimal,

    @Column(name = "quantity")
    val quantity: Int,

    @Column(name = "order_id")
    val orderId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal,
            sellingPrice: BigDecimal,
            quantity: Int = 1,
            orderId: Long,
            isRemoved: Boolean = false,
        ) = OrderProductEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            name = name,
            subtitle = subtitle,
            code = code,
            listPrice = listPrice,
            sellingPrice = sellingPrice,
            quantity = quantity,
            orderId = orderId,
        )
    }
}