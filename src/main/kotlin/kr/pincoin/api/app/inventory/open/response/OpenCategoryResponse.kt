package kr.pincoin.api.app.inventory.open.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpenCategoryResponse(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("created")
    val created: LocalDateTime?,

    @JsonProperty("modified")
    val modified: LocalDateTime?,

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

    @JsonProperty("lft")
    val lft: Int,

    @JsonProperty("rght")
    val rght: Int,

    @JsonProperty("treeId")
    val treeId: Int,

    @JsonProperty("level")
    val level: Int,

    @JsonProperty("parentId")
    val parentId: Long?,

    @JsonProperty("storeId")
    val storeId: Long,

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
    val naverMakerName: String,
)