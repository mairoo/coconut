package kr.co.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import java.math.BigDecimal

@Entity
@Table(name = "shop_orderproduct")
class OrderProductEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "order_id")
    val orderId: Long,

    @Column(name = "name")
    val name: String,

    @Column(name = "subtitle")
    val subtitle: String = "",

    @Column(name = "code")
    val code: String,

    @Column(name = "list_price", precision = 11, scale = 2)
    val listPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "selling_price", precision = 11, scale = 2)
    val sellingPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "quantity")
    val quantity: Int = 0,

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
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal = BigDecimal.ZERO,
            sellingPrice: BigDecimal = BigDecimal.ZERO,
            quantity: Int = 0,
        ) = OrderProductEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            orderId = orderId,
            name = name,
            subtitle = subtitle,
            code = code,
            listPrice = listPrice,
            sellingPrice = sellingPrice,
            quantity = quantity,
        )
    }
}