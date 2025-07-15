package kr.pincoin.api.app.user.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria

data class AdminUserSearchRequest(
    @JsonProperty("userId")
    val userId: Int? = null,

    @JsonProperty("username")
    val username: String? = null,

    @JsonProperty("firstName")
    val firstName: String? = null,

    @JsonProperty("lastName")
    val lastName: String? = null,

    @JsonProperty("email")
    val email: String? = null,

    @JsonProperty("isActive")
    val isActive: Boolean? = null,

    @JsonProperty("isSuperuser")
    val isSuperuser: Boolean? = null,
) {
    fun toSearchCriteria() = UserSearchCriteria(
        userId = userId?.toLong(),
        username = username,
        firstName = firstName,
        lastName = lastName,
        email = email,
        isActive = isActive,
        isSuperuser = isSuperuser,
    )
}