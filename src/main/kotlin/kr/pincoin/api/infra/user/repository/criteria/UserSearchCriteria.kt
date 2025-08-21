package kr.pincoin.api.infra.user.repository.criteria

import java.util.*

data class UserSearchCriteria(
    val userId: Int? = null,
    var username: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val email: String? = null,
    val isActive: Boolean? = null,
    val isSuperuser: Boolean? = null,
    val keycloakId: UUID? = null,
)