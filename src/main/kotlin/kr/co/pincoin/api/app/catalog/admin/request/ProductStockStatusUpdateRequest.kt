package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import kr.co.pincoin.api.domain.catalog.enums.ProductStock

data class ProductStockStatusUpdateRequest(
    @field:NotNull(message = "재고 상태는 필수 입력값입니다")
    @JsonProperty("stock")
    val stock: ProductStock
)