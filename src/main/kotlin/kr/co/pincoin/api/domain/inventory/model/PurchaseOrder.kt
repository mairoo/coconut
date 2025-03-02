package kr.co.pincoin.api.domain.inventory.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class PurchaseOrder private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 가변 필드
    val title: String,
    val content: String,
    val bankAccount: String?,
    val amount: BigDecimal,
    val paid: Boolean,
) {
    fun markAsPaid(): PurchaseOrder =
        copy(paid = true)

    fun markAsRemoved(): PurchaseOrder =
        copy(isRemoved = true)

    private fun copy(
        title: String? = null,
        content: String? = null,
        bankAccount: String? = this.bankAccount,
        amount: BigDecimal? = null,
        paid: Boolean? = null,
        isRemoved: Boolean? = null
    ): PurchaseOrder = PurchaseOrder(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        title = title ?: this.title,
        content = content ?: this.content,
        bankAccount = bankAccount,
        amount = amount ?: this.amount,
        paid = paid ?: this.paid
    )

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
                isRemoved = isRemoved ?: false,
                title = title,
                content = content,
                bankAccount = bankAccount,
                amount = amount,
                paid = paid,
            )
    }
}