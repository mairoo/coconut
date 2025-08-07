package kr.pincoin.api.app.inventory.open.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenCategorySearchRequest(
    @field:JsonProperty("title")
    var title: String? = null,

    @field:JsonProperty("slug")
    val slug: String? = null,

    @field:JsonProperty("parentId")
    val parentId: Int? = null,

    @field:JsonProperty("level")
    val level: Int? = null,
)