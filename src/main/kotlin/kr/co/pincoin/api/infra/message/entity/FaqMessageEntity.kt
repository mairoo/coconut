package kr.co.pincoin.api.infra.message.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.message.enums.FaqMessageCategory
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.message.converter.FaqMessageCategoryConverter

@Entity
@Table(name = "shop_faqmessage")
class FaqMessageEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "title")
    val title: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "keywords")
    val keywords: String,

    @Column(name = "content", columnDefinition = "text")
    val content: String,

    @Column(name = "category")
    @Convert(converter = FaqMessageCategoryConverter::class)
    val category: FaqMessageCategory,

    @Column(name = "position")
    val position: Int,

    @Column(name = "owner_id")
    val ownerId: Int? = null,

    @Column(name = "store_id")
    val storeId: Long,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            title: String,
            description: String,
            keywords: String,
            content: String,
            category: FaqMessageCategory,
            position: Int,
            ownerId: Int? = null,
            storeId: Long
        ) = FaqMessageEntity(
            id = id,
            title = title,
            description = description,
            keywords = keywords,
            content = content,
            category = category,
            position = position,
            ownerId = ownerId,
            storeId = storeId
        )
    }
}