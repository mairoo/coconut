package kr.co.pincoin.api.infra.inquiry.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.inquiry.enums.CustomerQuestionCategory
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.inquiry.converter.CustomerQuestionCategoryConverter

@Entity
@Table(name = "shop_customerquestion")
class CustomerQuestionEntity private constructor(
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
    @Convert(converter = CustomerQuestionCategoryConverter::class)
    val category: CustomerQuestionCategory,

    @Column(name = "order_id")
    val orderId: Long? = null,

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
            category: CustomerQuestionCategory,
            orderId: Long? = null,
            ownerId: Int? = null,
            storeId: Long
        ) = CustomerQuestionEntity(
            id = id,
            title = title,
            description = description,
            keywords = keywords,
            content = content,
            category = category,
            orderId = orderId,
            ownerId = ownerId,
            storeId = storeId
        )
    }
}