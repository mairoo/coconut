package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class ProductBasicInfoUpdateRequest(
    @field:Size(min = 2, max = 200, message = "상품명은 2자 이상 200자 이하로 입력해주세요")
    @JsonProperty("name")
    val name: String? = null,

    @field:Size(max = 300, message = "부제목은 최대 300자까지 입력 가능합니다")
    @JsonProperty("subtitle")
    val subtitle: String? = null,

    @field:Pattern(
        regexp = "^[A-Za-z0-9-_]+$",
        message = "상품 코드는 영문, 숫자, 하이픈(-), 밑줄(_)만 사용 가능합니다"
    )
    @field:Size(max = 50, message = "상품 코드는 최대 50자까지 입력 가능합니다")
    @JsonProperty("code")
    val code: String? = null
)
