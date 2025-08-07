package kr.pincoin.api.app.inventory.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.inventory.model.Category
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AdminCategoryResponse(
    @field:JsonProperty("id")
    val id: Long,

    @field:JsonProperty("created")
    val created: LocalDateTime?,

    @field:JsonProperty("modified")
    val modified: LocalDateTime?,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("slug")
    val slug: String,

    @field:JsonProperty("thumbnail")
    val thumbnail: String?,

    @field:JsonProperty("description")
    val description: String?,

    @field:JsonProperty("description1")
    val description1: String?,

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
    val naverSearchTag: String?,

    @field:JsonProperty("naverBrandName")
    val naverBrandName: String?,

    @field:JsonProperty("naverMakerName")
    val naverMakerName: String?,
) {
    companion object {
        fun from(category: Category) = with(category) {
            AdminCategoryResponse(
                id = id ?: throw IllegalStateException("카테고리 ID는 필수 입력값입니다"),
                created = created,
                modified = modified,
                title = title,
                slug = slug,
                thumbnail = thumbnail.takeIf { it.isNotBlank() },
                description = description.takeIf { it.isNotBlank() },
                description1 = description1.takeIf { it.isNotBlank() },
                lft = lft,
                rght = rght,
                treeId = treeId,
                level = level,
                parentId = parentId,
                storeId = storeId,
                discountRate = discountRate,
                pg = pg,
                pgDiscountRate = pgDiscountRate,
                naverSearchTag = naverSearchTag.takeIf { it.isNotBlank() },
                naverBrandName = naverBrandName.takeIf { it.isNotBlank() },
                naverMakerName = naverMakerName.takeIf { it.isNotBlank() },
            )
        }
    }
}