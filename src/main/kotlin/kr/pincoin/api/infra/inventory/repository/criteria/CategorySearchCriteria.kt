package kr.pincoin.api.infra.inventory.repository.criteria

data class CategorySearchCriteria(
    val categoryId: Long? = null,
    val title: String? = null,
    val slug: String? = null,
    val storeId: Long? = null,
    val parentId: Long? = null,
    val level: Int? = null,
    val treeId: Int? = null,
    val pg: Boolean? = null,
)