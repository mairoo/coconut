package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "shop_voucher")
class VoucherEntity private constructor(
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

    @Column(name = "remarks")
    val remarks: String,

    @Column(name = "status")
    val status: Int,

    @Column(name = "product_id")
    val productId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            code: String,
            remarks: String = "",
            status: Int = 0,
            productId: Long,
            isRemoved: Boolean = false,
        ) = VoucherEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            code = code,
            remarks = remarks,
            status = status,
            productId = productId,
        )
    }
}