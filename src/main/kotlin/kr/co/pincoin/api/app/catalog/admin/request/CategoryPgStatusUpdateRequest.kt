package kr.co.pincoin.api.app.catalog.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class CategoryPgStatusUpdateRequest(
    @field:NotNull(message = "PG 상태는 필수 입력값입니다")
    @JsonProperty("pg")
    val pg: Boolean
)