package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria

data class OpenCategorySearchRequest(
    @JsonProperty("categoryId")
    val categoryId: Long? = null,

    @JsonProperty("title")
    val title: String? = null,

    @JsonProperty("slug")
    val slug: String? = null,

    @JsonProperty("storeId")
    val storeId: Long? = null,

    @JsonProperty("parentId")
    val parentId: Long? = null,

    @JsonProperty("level")
    val level: Int? = null,

    @JsonProperty("treeId")
    val treeId: Int? = null,

    @JsonProperty("pg")
    val pg: Boolean? = null,
) {
    fun toSearchCriteria() = CategorySearchCriteria(
        categoryId = categoryId,
        title = title,
        slug = slug,
        storeId = storeId,
        parentId = parentId,
        level = level,
        treeId = treeId,
        pg = pg,
    )
}