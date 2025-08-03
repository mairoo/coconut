package kr.pincoin.api.app.user.admin.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern

data class AdminPasswordChangeRequest(
    @field:NotBlank(message = "새 비밀번호는 필수 입력값입니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
        message = "비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    val newPassword: String,

    /**
     * 임시 비밀번호 여부
     * true: 사용자가 다음 로그인 시 비밀번호 변경 필수
     * false: 영구 비밀번호로 설정
     */
    val temporary: Boolean = false,
)