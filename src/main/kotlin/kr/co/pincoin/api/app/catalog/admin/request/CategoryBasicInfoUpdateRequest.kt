package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CategoryBasicInfoUpdateRequest(
    @field:Size(min = 2, max = 100, message = "카테고리 제목은 2자 이상 100자 이하로 입력해주세요")
    @JsonProperty("title")
    val title: String? = null,

    @field:Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "슬러그는 영문 소문자, 숫자, 하이픈(-)만 사용 가능합니다"
    )
    @field:Size(max = 100, message = "카테고리 슬러그는 최대 100자까지 입력 가능합니다")
    @JsonProperty("slug")
    val slug: String? = null
)
