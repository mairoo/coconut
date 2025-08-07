package kr.pincoin.api.app.user.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class AdminUserSearchRequest(
    @field:JsonProperty("is_superuser")
    val isSuperuser: Boolean,

    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("first_name")
    val firstName: String,

    @field:JsonProperty("last_name")
    val lastName: String,

    @field:JsonProperty("email")
    val email: String,

    @field:JsonProperty("is_staff")
    val isStaff: Boolean,

    @field:JsonProperty("is_active")
    val isActive: Boolean,

    @field:JsonProperty("keycloak_id")
    val keycloakId: UUID? = null,
)