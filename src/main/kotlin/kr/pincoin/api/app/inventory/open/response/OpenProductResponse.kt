package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenProductResponse(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("created")
    val created: LocalDateTime?,

    @JsonProperty("modified")
    val modified: LocalDateTime?,

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

    @JsonProperty("naverPartner")
    val naverPartner: Boolean,

    @JsonProperty("naverPartnerTitle")
    val naverPartnerTitle: String,

    @JsonProperty("minimumStockLevel")
    val minimumStockLevel: Int,

    @JsonProperty("pg")
    val pg: Boolean,

    @JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal,

    @JsonProperty("naverAttribute")
    val naverAttribute: String,

    @JsonProperty("naverPartnerTitlePg")
    val naverPartnerTitlePg: String,

    @JsonProperty("reviewCountPg")
    val reviewCountPg: Int,

    @JsonProperty("maximumStockLevel")
    val maximumStockLevel: Int,

    @JsonProperty("stockQuantity")
    val stockQuantity: Int,
)