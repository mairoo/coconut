package kr.pincoin.api.domain.inventory.model

import java.math.BigDecimal
import java.time.LocalDateTime

class PurchaseOrder private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val title: String,
    val content: String = "",
    val bankAccount: String? = null,
    val amount: BigDecimal,
    val paid: Boolean = false,
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        title: String = this.title,
        content: String = this.content,
        bankAccount: String? = this.bankAccount,
        amount: BigDecimal = this.amount,
        paid: Boolean = this.paid,
    ): PurchaseOrder = PurchaseOrder(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        title = title,
        content = content,
        bankAccount = bankAccount,
        amount = amount,
        paid = paid,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            title: String,
            content: String = "",
            bankAccount: String? = null,
            amount: BigDecimal,
            paid: Boolean = false,
        ): PurchaseOrder = PurchaseOrder(
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