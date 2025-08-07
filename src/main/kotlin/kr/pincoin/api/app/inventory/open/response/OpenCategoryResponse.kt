package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.inventory.entity.CategoryEntity
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenCategoryResponse(
    @field:JsonProperty("id")
    val id: Long,

    @field:JsonProperty("created")
    val created: LocalDateTime,

    @field:JsonProperty("modified")
    val modified: LocalDateTime,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("slug")
    val slug: String,

    @field:JsonProperty("thumbnail")
    val thumbnail: String?,

    @field:JsonProperty("description")
    val description: String?,

    @field:JsonProperty("description1")
    val description1: String?,

    @field:JsonProperty("level")
    val level: Int,

    @field:JsonProperty("parentId")
    val parentId: Long?,

    @field:JsonProperty("discountRate")
    val discountRate: BigDecimal,

    @field:JsonProperty("pg")
    val pg: Boolean,

    @field:JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal,
) {
    companion object {
        fun from(category: CategoryEntity) = with(category) {
            OpenCategoryResponse(
                id = id ?: throw IllegalStateException("카테고리 ID는 필수 입력값입니다"),
                created = dateTimeFields.created,
                modified = dateTimeFields.modified,
                title = title,
                slug = slug,
                thumbnail = thumbnail.takeIf { it.isNotBlank() },
                description = description.takeIf { it.isNotBlank() },
                description1 = description1.takeIf { it.isNotBlank() },
                level = level,
                parentId = parentId,
                discountRate = discountRate,
                pg = pg,
                pgDiscountRate = pgDiscountRate,
            )
        }
    }
}