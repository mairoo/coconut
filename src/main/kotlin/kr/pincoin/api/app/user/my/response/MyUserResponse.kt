package kr.pincoin.api.app.user.my.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.user.model.User
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MyUserResponse(
    @JsonProperty("id")
    val id: Int,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("firstName")
    val firstName: String,

    @JsonProperty("lastName")
    val lastName: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("isActive")
    val isActive: Boolean,

    @JsonProperty("dateJoined")
    val dateJoined: LocalDateTime,

    @JsonProperty("lastLogin")
    val lastLogin: LocalDateTime?,
) {
    companion object {
        fun from(user: User) = with(user) {
            MyUserResponse(
                id = id ?: throw IllegalStateException("사용자 ID는 필수 입력값입니다"),
                username = username,
                firstName = firstName,
                lastName = lastName,
                email = email,
                isActive = isActive,
                dateJoined = dateJoined,
                lastLogin = lastLogin,
            )
        }
    }
}