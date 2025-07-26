package kr.pincoin.api.app.auth.vo

/**
 * Redis에서 조회한 임시 회원 가입 JSON 데이터를 매핑
 * 회원가입 2단계에서 사용자 생성에 필요한 모든 정보를 포함
 */
data class TemporarySignUpData(
    val email: String,
    val username: String,
    val firstName: String,
    val lastName: String,
    val encryptedPassword: String,
    val createdAt: String,
    val ipAddress: String,
    val userAgent: String,
    val acceptLanguage: String,
)