package kr.pincoin.api.app.user.common.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.user.model.User
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    @field:JsonProperty("id")
    val id: Int,

    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("firstName")
    val firstName: String,

    @field:JsonProperty("lastName")
    val lastName: String,

    @field:JsonProperty("email")
    val email: String,

    @field:JsonProperty("isActive")
    val isActive: Boolean,

    @field:JsonProperty("isStaff")
    val isStaff: Boolean,

    @field:JsonProperty("isSuperuser")
    val isSuperuser: Boolean,

    @field:JsonProperty("dateJoined")
    val dateJoined: LocalDateTime,

    @field:JsonProperty("lastLogin")
    val lastLogin: LocalDateTime?,

    @field:JsonProperty("keycloakId")
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