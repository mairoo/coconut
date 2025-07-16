package kr.pincoin.api.app.inventory.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.inventory.model.Product
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AdminProductResponse(
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

    @JsonProperty("description")
    val description: String,

    @JsonProperty("position")
    val position: Int,

    @JsonProperty("status")
    val status: Int,

    @JsonProperty("stock")
    val stock: Int,

    @JsonProperty("categoryId")
    val categoryId: Long,

    @JsonProperty("storeId")
    val storeId: Long,

    @JsonProperty("reviewCount")
    val reviewCount: Int,

    @JsonProperty("pg")
    val pg: Boolean,

    @JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal,

    @JsonProperty("reviewCountPg")
    val reviewCountPg: Int,

    @JsonProperty("stockQuantity")
    val stockQuantity: Int,

    @JsonProperty("created")
    val created: LocalDateTime?,
) {
    companion object {
        fun from(product: Product) = with(product) {
            AdminProductResponse(
                id = id ?: throw IllegalStateException("제품 ID는 필수 입력값입니다"),
                name = name,
                subtitle = subtitle,
                code = code,
                listPrice = listPrice,
                sellingPrice = sellingPrice,
                description = description,
                position = position,
                status = status,
                stock = stock,
                categoryId = categoryId,
                storeId = storeId,
                reviewCount = reviewCount,
                pg = pg,
                pgSellingPrice = pgSellingPrice,
                reviewCountPg = reviewCountPg,
                stockQuantity = stockQuantity,
                created = created,
            )
        }
    }
}