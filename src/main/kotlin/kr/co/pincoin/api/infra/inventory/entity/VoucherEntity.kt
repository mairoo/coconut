package kr.co.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.inventory.converter.VoucherStatusConverter

@Entity
@Table(name = "shop_voucher")
class VoucherEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "code")
    val code: String,

    @Column(name = "remarks")
    val remarks: String,

    @Column(name = "status")
    @Convert(converter = VoucherStatusConverter::class)
    val status: VoucherStatus,

    @Column(name = "product_id")
    val productId: Long,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
            code: String,
            remarks: String,
            status: VoucherStatus = VoucherStatus.PURCHASED,
            productId: Long,
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
