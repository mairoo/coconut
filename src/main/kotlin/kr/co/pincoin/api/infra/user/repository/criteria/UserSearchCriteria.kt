package kr.co.pincoin.api.infra.user.repository.criteria

data class UserSearchCriteria(
    val email: String? = null,
    val username: String? = null,
    val isActive: Boolean? = null,
    val isSuperuser: Boolean? = null,
)