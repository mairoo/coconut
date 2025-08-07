package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenProductSearchRequest(
    @field:JsonProperty("name")
    val name: String? = null,

    @field:JsonProperty("subtitle")
    val subtitle: String? = null,

    @field:JsonProperty("code")
    val code: String? = null,

    @field:JsonProperty("position")
    val position: Int? = null,

    @field:JsonProperty("status")
    val status: Int? = null,

    @field:JsonProperty("stock")
    val stock: Int? = null,

    @field:JsonProperty("isRemoved")
    var isRemoved: Boolean? = null,
)