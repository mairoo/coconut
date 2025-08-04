package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenCategorySearchRequest(
    @JsonProperty("title")
    var title: String? = null,

    @JsonProperty("slug")
    val slug: String? = null,

    @JsonProperty("parentId")
    val parentId: Int? = null,

    @JsonProperty("level")
    val level: Int? = null,
)