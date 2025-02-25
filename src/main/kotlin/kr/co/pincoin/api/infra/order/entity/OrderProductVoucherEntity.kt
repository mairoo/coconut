package kr.co.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "shop_orderproductvoucher")
class OrderProductVoucherEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "order_product_id", nullable = false)
    val orderProductId: Long,

    @Column(name = "voucher_id")
    val voucherId: Long? = null,

    @Column(name = "code", nullable = false)
    val code: String,

    @Column(name = "revoked")
    val revoked: Boolean = false,

    @Column(name = "remarks")
    val remarks: String = "",

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
            orderProductId: Long,
            voucherId: Long? = null,
            code: String,
            revoked: Boolean = false,
            remarks: String = "",
        ) = OrderProductVoucherEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            orderProductId = orderProductId,
            voucherId = voucherId,
            code = code,
            revoked = revoked,
            remarks = remarks,
        )
    }
}