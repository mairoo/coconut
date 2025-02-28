package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ProductPositionUpdateRequest(
    @field:NotNull(message = "상품 위치는 필수 입력값입니다")
    @field:Min(value = 0, message = "상품 위치는 0 이상이어야 합니다")
    @JsonProperty("position")
    val position: Int
)
