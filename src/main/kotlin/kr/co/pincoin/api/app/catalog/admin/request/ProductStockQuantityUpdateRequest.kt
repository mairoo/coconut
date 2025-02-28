package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

data class ProductStockQuantityUpdateRequest(
    @field:NotNull(message = "재고 수량은 필수 입력값입니다")
    @field:Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @JsonProperty("stockQuantity")
    val stockQuantity: Int
)