package kr.co.pincoin.api.domain.inventory.model

import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus
import java.time.ZonedDateTime

class Voucher private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드

    // 4. 도메인 로직 가변 필드
    val code: String,
    val remarks: String,
    val productId: Long,
    val status: VoucherStatus,
) {
    fun update(
        newCode: String? = null,
        newRemarks: String? = null
    ): Voucher =
        copy(
            code = newCode ?: code,
            remarks = newRemarks ?: remarks
        )

    fun updateProduct(newProductId: Long? = null): Voucher =
        copy(productId = newProductId ?: productId)

    fun updateStatus(newStatus: VoucherStatus? = null): Voucher =
        copy(status = newStatus ?: status)

    fun markAsRemoved(): Voucher =
        copy(isRemoved = true)

    private fun copy(
        code: String? = null,
        remarks: String? = null,
        productId: Long? = null,
        status: VoucherStatus? = null,
        isRemoved: Boolean? = null
    ): Voucher = Voucher(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        code = code ?: this.code,
        remarks = remarks ?: this.remarks,
        productId = productId ?: this.productId,
        status = status ?: this.status
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            code: String,
            remarks: String,
            productId: Long,
            status: VoucherStatus,
        ): Voucher =
            Voucher(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                code = code,
                remarks = remarks,
                productId = productId,
                status = status,
            )
    }
}