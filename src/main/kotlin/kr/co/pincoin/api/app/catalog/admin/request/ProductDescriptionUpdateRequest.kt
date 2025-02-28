package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Size

data class ProductDescriptionUpdateRequest(
    @field:Size(max = 5000, message = "상품 설명은 최대 5000자까지 입력 가능합니다")
    @JsonProperty("description")
    val description: String? = null
)
