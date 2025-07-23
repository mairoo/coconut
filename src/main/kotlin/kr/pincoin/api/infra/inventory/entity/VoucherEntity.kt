package kr.pincoin.api.infra.inventory.entity

import jakarta.persistence.*
import kr.pincoin.api.domain.inventory.enums.VoucherStatus
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import kr.pincoin.api.infra.inventory.converter.VoucherStatusConverter

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
    @Convert(converter = VoucherStatusConverter::class)
    val status: VoucherStatus,

    @Column(name = "product_id")
    val productId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            code: String,
            remarks: String = "",
            status: VoucherStatus = VoucherStatus.PURCHASED,
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