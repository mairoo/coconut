package kr.pincoin.api.app.user.my.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class MyPasswordChangeRequest(
    @field:NotBlank(message = "현재 비밀번호는 필수 입력값입니다")
    val currentPassword: String,

    @field:NotBlank(message = "새 비밀번호는 필수 입력값입니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
        message = "비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    val newPassword: String,
)