package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class CategoryThumbnailUpdateRequest(
    @field:Size(max = 255, message = "썸네일 URL은 최대 255자까지 입력 가능합니다")
    @JsonProperty("thumbnail")
    val thumbnail: String? = null
)