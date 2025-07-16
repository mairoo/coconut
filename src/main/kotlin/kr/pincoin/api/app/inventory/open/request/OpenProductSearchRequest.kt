package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria

data class OpenProductSearchRequest(
    @JsonProperty("productId")
    val productId: Long? = null,

    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("subtitle")
    val subtitle: String? = null,

    @JsonProperty("code")
    val code: String? = null,

    @JsonProperty("categoryId")
    val categoryId: Long? = null,

    @JsonProperty("storeId")
    val storeId: Long? = null,

    @JsonProperty("status")
    val status: Int? = null,

    @JsonProperty("pg")
    val pg: Boolean? = null,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean? = false,
) {
    fun toSearchCriteria() = ProductSearchCriteria(
        productId = productId,
        name = name,
        subtitle = subtitle,
        code = code,
        categoryId = categoryId,
        storeId = storeId,
        status = status,
        pg = pg,
        isRemoved = isRemoved,
    )
}