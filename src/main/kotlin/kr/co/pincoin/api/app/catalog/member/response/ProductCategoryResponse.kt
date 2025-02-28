package kr.co.pincoin.api.app.catalog.member.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductCategoryResponse(
    // Product 정보
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("name")
    val name: String,

    @JsonProperty("subtitle")
    val subtitle: String,

    @JsonProperty("code")
    val code: String,

    @JsonProperty("listPrice")
    val listPrice: BigDecimal,

    @JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal,

    @JsonProperty("pg")
    val pg: Boolean,

    @JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal,

    @JsonProperty("description")
    val description: String,

    @JsonProperty("categoryId")
    val categoryId: Long,

    @JsonProperty("position")
    val position: Int,

    // Category 정보
    @JsonProperty("categoryTitle")
    val categoryTitle: String,

    @JsonProperty("categorySlug")
    val categorySlug: String,

    @JsonProperty("categoryThumbnail")
    val categoryThumbnail: String,

    @JsonProperty("categoryDescription")
    val categoryDescription: String,

    @JsonProperty("categoryDiscountRate")
    val categoryDiscountRate: BigDecimal
) {
    companion object {
        fun from(projection: ProductCategoryProjection): ProductCategoryResponse = with(projection) {
            ProductCategoryResponse(
                id = id!!,
                name = name,
                subtitle = subtitle,
                code = code,
                listPrice = listPrice,
                sellingPrice = sellingPrice,
                pg = pg,
                pgSellingPrice = pgSellingPrice,
                description = description,
                categoryId = categoryId,
                position = position,
                categoryTitle = categoryTitle,
                categorySlug = categorySlug,
                categoryThumbnail = categoryThumbnail,
                categoryDescription = categoryDescription,
                categoryDiscountRate = categoryDiscountRate
            )
        }
    }
}
