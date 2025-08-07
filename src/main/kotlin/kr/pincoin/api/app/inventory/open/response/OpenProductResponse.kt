package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenProductResponse(
    @field:JsonProperty("id")
    val id: Long?,

    @field:JsonProperty("created")
    val created: LocalDateTime?,

    @field:JsonProperty("modified")
    val modified: LocalDateTime?,

    @field:JsonProperty("isRemoved")
    val isRemoved: Boolean,

    @field:JsonProperty("name")
    val name: String,

    @field:JsonProperty("subtitle")
    val subtitle: String,

    @field:JsonProperty("code")
    val code: String,

    @field:JsonProperty("listPrice")
    val listPrice: BigDecimal,

    @field:JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal,

    @field:JsonProperty("description")
    val description: String,

    @field:JsonProperty("position")
    val position: Int,

    @field:JsonProperty("status")
    val status: Int,

    @field:JsonProperty("stock")
    val stock: Int,

    @field:JsonProperty("categoryId")
    val categoryId: Long,

    @field:JsonProperty("storeId")
    val storeId: Long,

    @field:JsonProperty("reviewCount")
    val reviewCount: Int,

    @field:JsonProperty("naverPartner")
    val naverPartner: Boolean,

    @field:JsonProperty("naverPartnerTitle")
    val naverPartnerTitle: String,

    @field:JsonProperty("minimumStockLevel")
    val minimumStockLevel: Int,

    @field:JsonProperty("pg")
    val pg: Boolean,

    @field:JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal,

    @field:JsonProperty("naverAttribute")
    val naverAttribute: String,

    @field:JsonProperty("naverPartnerTitlePg")
    val naverPartnerTitlePg: String,

    @field:JsonProperty("reviewCountPg")
    val reviewCountPg: Int,

    @field:JsonProperty("maximumStockLevel")
    val maximumStockLevel: Int,

    @field:JsonProperty("stockQuantity")
    val stockQuantity: Int,
)