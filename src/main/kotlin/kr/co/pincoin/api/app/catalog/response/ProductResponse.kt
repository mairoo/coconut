package kr.co.pincoin.api.app.catalog.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.model.Product
import java.math.BigDecimal
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("created")
    val created: ZonedDateTime,

    @JsonProperty("modified")
    val modified: ZonedDateTime,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean,

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

    @JsonProperty("status")
    val status: ProductStatus,

    @JsonProperty("stockQuantity")
    val stockQuantity: Int,

    @JsonProperty("stock")
    val stock: ProductStock,
) {
    companion object {
        fun from(product: Product): ProductResponse = with(product) {
            ProductResponse(
                id = id!!,
                created = created!!,
                modified = modified!!,
                isRemoved = isRemoved,
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
                status = status,
                stockQuantity = stockQuantity,
                stock = stock,
            )
        }
    }
}
