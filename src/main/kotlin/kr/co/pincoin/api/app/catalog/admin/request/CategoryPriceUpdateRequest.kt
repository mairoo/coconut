package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class CategoryPriceUpdateRequest(
    @field:DecimalMin(value = "0.0", message = "할인율은 0 이상이어야 합니다")
    @field:DecimalMax(value = "100.0", message = "할인율은 100 이하여야 합니다")
    @JsonProperty("discountRate")
    val discountRate: BigDecimal? = null,

    @field:DecimalMin(value = "0.0", message = "PG 할인율은 0 이상이어야 합니다")
    @field:DecimalMax(value = "100.0", message = "PG 할인율은 100 이하여야 합니다")
    @JsonProperty("pgDiscountRate")
    val pgDiscountRate: BigDecimal? = null
)