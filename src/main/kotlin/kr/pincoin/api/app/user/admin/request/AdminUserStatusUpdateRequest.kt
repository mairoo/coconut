package kr.pincoin.api.app.user.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotNull

data class AdminUserStatusUpdateRequest(
    @field:NotNull(message = "활성화 상태는 필수 입력값입니다")
    @JsonProperty("isActive")
    val isActive: Boolean,
)