package kr.pincoin.api.app.inventory.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class AdminCategorySearchRequest(
    @field:JsonProperty("categoryId")
    val categoryId: Long? = null,

    @field:JsonProperty("title")
    val title: String? = null,

    @field:JsonProperty("slug")
    val slug: String? = null,

    @field:JsonProperty("description")
    val description: String? = null,

    @field:JsonProperty("description1")
    val description1: String? = null,

    @field:JsonProperty("lft")
    val lft: Int? = null,

    @field:JsonProperty("rght")
    val rght: Int? = null,

    @field:JsonProperty("treeId")
    val treeId: Int? = null,

    @field:JsonProperty("level")
    val level: Int? = null,

    @field:JsonProperty("parentId")
    val parentId: Long? = null,

    @field:JsonProperty("discountRate")
    val discountRate: BigDecimal? = null,

    @field:JsonProperty("pg")
    val pg: Boolean? = null,

    @field:JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal? = null,

    @field:JsonProperty("naverSearchTag")
    val naverSearchTag: String? = null,

    @field:JsonProperty("naverBrandName")
    val naverBrandName: String? = null,

    @field:JsonProperty("naverMakerName")
    val naverMakerName: String? = null,
)