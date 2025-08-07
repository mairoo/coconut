package kr.pincoin.api.app.inventory.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class AdminProductSearchRequest(
    @field:JsonProperty("productId")
    val productId: Long? = null,

    @field:JsonProperty("name")
    val name: String? = null,

    @field:JsonProperty("subtitle")
    val subtitle: String? = null,

    @field:JsonProperty("code")
    val code: String? = null,

    @field:JsonProperty("listPrice")
    val listPrice: BigDecimal? = null,

    @field:JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal? = null,

    @field:JsonProperty("description")
    val description: String? = null,

    @field:JsonProperty("position")
    val position: Int? = null,

    @field:JsonProperty("status")
    val status: Int? = null,

    @field:JsonProperty("stock")
    val stock: Int? = null,

    @field:JsonProperty("categoryId")
    val categoryId: Long? = null,

    @field:JsonProperty("reviewCount")
    val reviewCount: Int? = null,

    @field:JsonProperty("naverPartner")
    val naverPartner: Boolean? = null,

    @field:JsonProperty("naverPartnerTitle")
    val naverPartnerTitle: String? = null,

    @field:JsonProperty("minimumStockLevel")
    val minimumStockLevel: Int? = null,

    @field:JsonProperty("pg")
    val pg: Boolean? = null,

    @field:JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal? = null,

    @field:JsonProperty("naverAttribute")
    val naverAttribute: String? = null,

    @field:JsonProperty("naverPartnerTitlePg")
    val naverPartnerTitlePg: String? = null,

    @field:JsonProperty("reviewCountPg")
    val reviewCountPg: Int? = null,

    @field:JsonProperty("maximumStockLevel")
    val maximumStockLevel: Int? = null,

    @field:JsonProperty("stockQuantity")
    val stockQuantity: Int? = null,

    @field:JsonProperty("isRemoved")
    val isRemoved: Boolean? = null,
)