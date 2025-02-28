package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class CategoryDescriptionUpdateRequest(
    @field:Size(max = 1000, message = "설명은 최대 1000자까지 입력 가능합니다")
    @JsonProperty("description")
    val description: String? = null,

    @field:Size(max = 5000, message = "상세 설명은 최대 5000자까지 입력 가능합니다")
    @JsonProperty("description1")
    val description1: String? = null
)
