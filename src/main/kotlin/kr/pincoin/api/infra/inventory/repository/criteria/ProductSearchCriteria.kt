package kr.pincoin.api.infra.inventory.repository.criteria

data class ProductSearchCriteria(
    val productId: Long? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val code: String? = null,
    val categoryId: Long? = null,
    val storeId: Long? = null,
    val status: Int? = null,
    val pg: Boolean? = null,
    val isRemoved: Boolean? = null,
)