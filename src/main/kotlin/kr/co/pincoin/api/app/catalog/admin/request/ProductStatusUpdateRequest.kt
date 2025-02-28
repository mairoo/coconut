package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus

data class ProductStatusUpdateRequest(
    @field:NotNull(message = "상품 상태는 필수 입력값입니다")
    @JsonProperty("status")
    val status: ProductStatus
)