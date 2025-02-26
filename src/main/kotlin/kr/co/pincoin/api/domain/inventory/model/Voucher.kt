package kr.co.pincoin.api.domain.inventory.model

import kr.co.pincoin.api.domain.inventory.enums.VoucherStatus
import java.time.ZonedDateTime

class Voucher private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드

    // 4. 도메인 로직 가변 필드
    code: String,
    remarks: String,
    productId: Long,
    status: VoucherStatus,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var code: String = code
        private set

    var remarks: String = remarks
        private set

    var productId: Long = productId
        private set

    var status: VoucherStatus = status
        private set

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
                isRemoved = isRemoved,
                code = code,
                remarks = remarks,
                productId = productId,
                status = status,
            )
    }
}