package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class CategoryNaverInfoUpdateRequest(
    @field:Size(max = 200, message = "네이버 검색 태그는 최대 200자까지 입력 가능합니다")
    @JsonProperty("naverSearchTag")
    val naverSearchTag: String? = null,

    @field:Size(max = 100, message = "네이버 브랜드명은 최대 100자까지 입력 가능합니다")
    @JsonProperty("naverBrandName")
    val naverBrandName: String? = null,

    @field:Size(max = 100, message = "네이버 제조사명은 최대 100자까지 입력 가능합니다")
    @JsonProperty("naverMakerName")
    val naverMakerName: String? = null
)