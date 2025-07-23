package kr.pincoin.api.app.user.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AdminUserCreateRequest(
    @field:NotBlank(message = "사용자명은 필수 입력값입니다")
    @field:Size(min = 3, max = 30, message = "사용자명은 3자 이상 30자 이하로 입력해주세요")
    @JsonProperty("username")
    val username: String,

    @field:NotBlank(message = "비밀번호는 필수 입력값입니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
        message = "비밀번호는 8~30자리이면서 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    @JsonProperty("password")
    val password: String,

    @field:NotBlank(message = "이메일은 필수 입력값입니다")
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @JsonProperty("email")
    val email: String,

    @field:Size(max = 30, message = "이름은 30자 이하로 입력해주세요")
    @JsonProperty("firstName")
    val firstName: String = "",

    @field:Size(max = 30, message = "성은 30자 이하로 입력해주세요")
    @JsonProperty("lastName")
    val lastName: String = "",

    @JsonProperty("isStaff")
    val isStaff: Boolean = false,

    @JsonProperty("isSuperuser")
    val isSuperuser: Boolean = false,

    @JsonProperty("isActive")
    val isActive: Boolean = true,
)