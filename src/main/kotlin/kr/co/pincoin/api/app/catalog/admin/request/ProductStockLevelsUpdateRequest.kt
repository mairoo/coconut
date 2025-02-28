package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Min

data class ProductStockLevelsUpdateRequest(
    @field:Min(value = 0, message = "최소 재고 수량은 0 이상이어야 합니다")
    @JsonProperty("minimumStockLevel")
    val minimumStockLevel: Int? = null,

    @field:Min(value = 0, message = "최대 재고 수량은 0 이상이어야 합니다")
    @JsonProperty("maximumStockLevel")
    val maximumStockLevel: Int? = null
)