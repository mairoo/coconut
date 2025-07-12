package kr.pincoin.api.infra.support.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "shop_customerquestion")
class CustomerQuestionEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "title")
    val title: String,

    @Column(name = "description")
    val description: String,

    @Column(name = "keywords")
    val keywords: String,

    @Column(name = "content")
    val content: String,

    @Column(name = "category")
    val category: Int,

    @Column(name = "order_id")
    val orderId: Long?,

    @Column(name = "owner_id")
    val ownerId: Int?,

    @Column(name = "store_id")
    val storeId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            title: String,
            description: String = "",
            keywords: String = "",
            content: String,
            category: Int = 0,
            orderId: Long? = null,
            ownerId: Int? = null,
            storeId: Long,
            isRemoved: Boolean = false,
        ) = CustomerQuestionEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
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