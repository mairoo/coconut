package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.DecimalMin
import java.math.BigDecimal

data class ProductPriceUpdateRequest(
    @field:DecimalMin(value = "0.0", inclusive = false, message = "정가는 0보다 커야 합니다")
    @JsonProperty("listPrice")
    val listPrice: BigDecimal? = null,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "판매가는 0보다 커야 합니다")
    @JsonProperty("sellingPrice")
    val sellingPrice: BigDecimal? = null,

    @field:DecimalMin(value = "0.0", inclusive = false, message = "PG 판매가는 0보다 커야 합니다")
    @JsonProperty("pgSellingPrice")
    val pgSellingPrice: BigDecimal? = null
)
