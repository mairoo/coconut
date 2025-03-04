package kr.co.pincoin.api.domain.inquiry.model

import kr.co.pincoin.api.domain.inquiry.enums.CustomerQuestionCategory
import java.time.ZonedDateTime

class CustomerQuestion private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    // 4. 도메인 로직 가변 필드
    val title: String,
    val description: String,
    val keywords: String,
    val content: String,
    val category: CustomerQuestionCategory,
    val orderId: Long?,
    val ownerId: Int?,
) {
    fun update(
        newTitle: String? = null,
        newDescription: String? = null,
        newKeywords: String? = null,
        newContent: String? = null,
        newCategory: CustomerQuestionCategory? = null
    ): CustomerQuestion =
        copy(
            title = newTitle ?: title,
            description = newDescription ?: description,
            keywords = newKeywords ?: keywords,
            content = newContent ?: content,
            category = newCategory ?: category
        )

    fun updateOwnership(
        newOrderId: Long? = null,
        newOwnerId: Int? = null
    ): CustomerQuestion =
        copy(
            orderId = newOrderId ?: orderId,
            ownerId = newOwnerId ?: ownerId
        )

    fun remove(): CustomerQuestion =
        copy(isRemoved = true)

    private fun copy(
        title: String? = null,
        description: String? = null,
        keywords: String? = null,
        content: String? = null,
        category: CustomerQuestionCategory? = null,
        orderId: Long? = null,
        ownerId: Int? = null,
        isRemoved: Boolean? = null
    ): CustomerQuestion = CustomerQuestion(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        title = title ?: this.title,
        description = description ?: this.description,
        keywords = keywords ?: this.keywords,
        content = content ?: this.content,
        category = category ?: this.category,
        orderId = orderId ?: this.orderId,
        ownerId = ownerId ?: this.ownerId
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            title: String,
            description: String,
            keywords: String,
            content: String,
            category: CustomerQuestionCategory,
            orderId: Long? = null,
            ownerId: Int? = null,
        ): CustomerQuestion =
            CustomerQuestion(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                title = title,
                description = description,
                keywords = keywords,
                content = content,
                category = category,
                orderId = orderId,
                ownerId = ownerId,
            )
    }
}