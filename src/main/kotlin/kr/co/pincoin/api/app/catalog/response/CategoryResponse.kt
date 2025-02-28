package kr.co.pincoin.api.app.catalog.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.catalog.model.Category
import java.math.BigDecimal
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CategoryResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("created")
    val created: ZonedDateTime,

    @JsonProperty("modified")
    val modified: ZonedDateTime,

    @JsonProperty("title")
    val title: String,

    @JsonProperty("slug")
    val slug: String,

    @JsonProperty("thumbnail")
    val thumbnail: String,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("description1")
    val description1: String,

    @JsonProperty("discountRate")
    val discountRate: BigDecimal,

    @JsonProperty("pg")
    val pg: Boolean,

    @JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal,

    @JsonProperty("naverSearchTag")
    val naverSearchTag: String,

    @JsonProperty("naverBrandName")
    val naverBrandName: String,

    @JsonProperty("naverMakerName")
    val naverMakerName: String
) {
    companion object {
        fun from(category: Category): CategoryResponse = with(category) {
            CategoryResponse(
                id = id!!,
                created = created!!,
                modified = modified!!,
                title = title,
                slug = slug,
                thumbnail = thumbnail,
                description = description,
                description1 = description1,
                discountRate = discountRate,
                pg = pg,
                pgDiscountRate = pgDiscountRate,
                naverSearchTag = naverSearchTag,
                naverBrandName = naverBrandName,
                naverMakerName = naverMakerName
            )
        }
    }
}