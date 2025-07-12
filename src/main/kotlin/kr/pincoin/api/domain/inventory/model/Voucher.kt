package kr.pincoin.api.domain.inventory.model

import java.time.LocalDateTime

class Voucher private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val code: String,
    val remarks: String = "",
    val status: Int = 0,
    val productId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            code: String,
            remarks: String = "",
            status: Int = 0,
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