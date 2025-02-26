package kr.co.pincoin.api.domain.inventory.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class PurchaseOrder private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 가변 필드
    title: String,
    content: String,
    bankAccount: String?,
    amount: BigDecimal,
    paid: Boolean,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var title: String = title
        private set

    var content: String = content
        private set

    var bankAccount: String? = bankAccount
        private set

    var amount: BigDecimal = amount
        private set

    var paid: Boolean = paid
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            title: String,
            content: String,
            bankAccount: String? = null,
            amount: BigDecimal,
            paid: Boolean = false,
        ): PurchaseOrder =
            PurchaseOrder(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                title = title,
                content = content,
                bankAccount = bankAccount,
                amount = amount,
                paid = paid,
            )
    }
}