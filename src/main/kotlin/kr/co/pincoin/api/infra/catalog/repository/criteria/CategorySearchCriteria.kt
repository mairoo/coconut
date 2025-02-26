package kr.co.pincoin.api.infra.catalog.repository.criteria

data class CategorySearchCriteria(
    val title: String? = null,
    val slug: String? = null,
    val storeId: Long? = null,
    val description: String? = null,
    val pg: Boolean? = null
)