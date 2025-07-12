package kr.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "shop_orderproductvoucher")
class OrderProductVoucherEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "code")
    val code: String,

    @Column(name = "revoked")
    val revoked: Boolean,

    @Column(name = "remarks")
    val remarks: String,

    @Column(name = "order_product_id")
    val orderProductId: Long,

    @Column(name = "voucher_id")
    val voucherId: Long?,
) {
    companion object {
        fun of(
            id: Long? = null,
            code: String,
            revoked: Boolean = false,
            remarks: String = "",
            orderProductId: Long,
            voucherId: Long? = null,
            isRemoved: Boolean = false,
        ) = OrderProductVoucherEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            code = code,
            revoked = revoked,
            remarks = remarks,
            orderProductId = orderProductId,
            voucherId = voucherId,
        )
    }
}