package kr.pincoin.api.domain.support.model

import java.time.LocalDateTime

class CustomerQuestion private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val title: String,
    val description: String = "",
    val keywords: String = "",
    val content: String,
    val category: Int = 0,
    val orderId: Long? = null,
    val ownerId: Int? = null,
    val storeId: Long,
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        title: String = this.title,
        description: String = this.description,
        keywords: String = this.keywords,
        content: String = this.content,
        category: Int = this.category,
        orderId: Long? = this.orderId,
        ownerId: Int? = this.ownerId,
        storeId: Long = this.storeId,
    ): CustomerQuestion = CustomerQuestion(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        title = title,
        description = description,
        keywords = keywords,
        content = content,
        category = category,
        orderId = orderId,
        ownerId = ownerId,
        storeId = storeId,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            title: String,
            description: String = "",
            keywords: String = "",
            content: String,
            category: Int = 0,
            orderId: Long? = null,
            ownerId: Int? = null,
            storeId: Long,
        ): CustomerQuestion = CustomerQuestion(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            title = title,
            description = description,
            keywords = keywords,
            content = content,
            category = category,
            orderId = orderId,
            ownerId = ownerId,
            storeId = storeId,
        )
    }
}