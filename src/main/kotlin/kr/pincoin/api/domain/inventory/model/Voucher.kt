package kr.pincoin.api.domain.inventory.model

import kr.pincoin.api.domain.inventory.enums.VoucherStatus
import java.time.LocalDateTime

class Voucher private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val code: String,
    val remarks: String = "",
    val status: VoucherStatus = VoucherStatus.PURCHASED,
    val productId: Long,
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        code: String = this.code,
        remarks: String = this.remarks,
        status: VoucherStatus = this.status,
        productId: Long = this.productId,
    ): Voucher = Voucher(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        code = code,
        remarks = remarks,
        status = status,
        productId = productId,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            code: String,
            remarks: String = "",
            status: VoucherStatus = VoucherStatus.PURCHASED,
            productId: Long,
        ): Voucher = Voucher(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            code = code,
            remarks = remarks,
            status = status,
            productId = productId,
        )
    }
}