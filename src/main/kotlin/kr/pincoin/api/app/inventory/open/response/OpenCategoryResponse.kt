package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.inventory.model.Category
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenCategoryResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("slug")
    val slug: String,

    @JsonProperty("thumbnail")
    val thumbnail: String,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("discountRate")
    val discountRate: BigDecimal,

    @JsonProperty("pg")
    val pg: Boolean,

    @JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal,

    @JsonProperty("created")
    val created: LocalDateTime?,
) {
    companion object {
        fun from(category: Category) = with(category) {
            OpenCategoryResponse(
                id = id ?: throw IllegalStateException("카테고리 ID는 필수 입력값입니다"),
                title = title,
                slug = slug,
                thumbnail = thumbnail,
                description = description,
                discountRate = discountRate,
                pg = pg,
                pgDiscountRate = pgDiscountRate,
                created = created,
            )
        }
    }
}