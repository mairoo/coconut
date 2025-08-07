package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenCategoryResponse(
    @field:JsonProperty("id")
    val id: Long?,

    @field:JsonProperty("created")
    val created: LocalDateTime?,

    @field:JsonProperty("modified")
    val modified: LocalDateTime?,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("slug")
    val slug: String,

    @field:JsonProperty("thumbnail")
    val thumbnail: String,

    @field:JsonProperty("description")
    val description: String,

    @field:JsonProperty("description1")
    val description1: String,

    @field:JsonProperty("lft")
    val lft: Int,

    @field:JsonProperty("rght")
    val rght: Int,

    @field:JsonProperty("treeId")
    val treeId: Int,

    @field:JsonProperty("level")
    val level: Int,

    @field:JsonProperty("parentId")
    val parentId: Long?,

    @field:JsonProperty("storeId")
    val storeId: Long,

    @field:JsonProperty("discountRate")
    val discountRate: BigDecimal,

    @field:JsonProperty("pg")
    val pg: Boolean,

    @field:JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal,

    @field:JsonProperty("naverSearchTag")
    val naverSearchTag: String,

    @field:JsonProperty("naverBrandName")
    val naverBrandName: String,

    @field:JsonProperty("naverMakerName")
    val naverMakerName: String,
)