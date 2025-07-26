package kr.pincoin.api.app.auth.vo

/**
 * Keycloak 로그인 결과 데이터 클래스
 */
data class KeycloakLoginResult(
    val accessToken: String,
    val refreshToken: String?,
    val expiresIn: Long,
    val refreshExpiresIn: Long?,
)