package kr.pincoin.api.app.user.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class AdminUserSearchRequest(
    @JsonProperty("is_superuser")
    val isSuperuser: Boolean,

    @JsonProperty("username")
    val username: String,

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String,

    @JsonProperty("email")
    val email: String,

    @JsonProperty("is_staff")
    val isStaff: Boolean,

    @JsonProperty("is_active")
    val isActive: Boolean,

    @JsonProperty("keycloak_id")
    val keycloakId: UUID? = null,
)