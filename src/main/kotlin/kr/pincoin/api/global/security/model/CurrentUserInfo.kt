package kr.pincoin.api.global.security.model

/**
 * 현재 인증된 사용자 정보
 */
data class CurrentUserInfo(
    val keycloakId: String,
    val email: String,
    val username: String?,
    val roles: List<String>
) {
    /**
     * 특정 역할을 가지고 있는지 확인
     */
    fun hasRole(role: String): Boolean = roles.contains(role)

    /**
     * 관리자 권한 확인
     */
    fun isAdmin(): Boolean = hasRole("ADMIN") || hasRole("admin")
}