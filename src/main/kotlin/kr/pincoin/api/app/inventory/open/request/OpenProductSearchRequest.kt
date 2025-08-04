package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenProductSearchRequest(
    @JsonProperty("name")
    val name: String? = null,

    @JsonProperty("subtitle")
    val subtitle: String? = null,

    @JsonProperty("code")
    val code: String? = null,

    @JsonProperty("position")
    val position: Int? = null,

    @JsonProperty("status")
    val status: Int? = null,

    @JsonProperty("stock")
    val stock: Int? = null,

    @JsonProperty("isRemoved")
    var isRemoved: Boolean? = null,
)