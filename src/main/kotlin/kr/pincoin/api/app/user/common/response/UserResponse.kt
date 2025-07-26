package kr.pincoin.api.app.user.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.user.model.User
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
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

    @JsonProperty("isStaff")
    val isStaff: Boolean,

    @JsonProperty("isSuperuser")
    val isSuperuser: Boolean,

    @JsonProperty("dateJoined")
    val dateJoined: LocalDateTime,

    @JsonProperty("lastLogin")
    val lastLogin: LocalDateTime?,

    @JsonProperty("keycloakId")
    val keycloakId: UUID?,
) {
    companion object {
        fun from(user: User) = with(user) {
            UserResponse(
                id = id ?: throw IllegalStateException("사용자 ID는 필수 입력값입니다"),
                username = username,
                firstName = firstName,
                lastName = lastName,
                email = email,
                isActive = isActive,
                isStaff = isStaff,
                isSuperuser = isSuperuser,
                dateJoined = dateJoined,
                lastLogin = lastLogin,
                keycloakId = keycloakId,
            )
        }
    }
}