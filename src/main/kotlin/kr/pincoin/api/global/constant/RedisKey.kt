package kr.pincoin.api.global.constant

object RedisKey {
    const val EMAIL = "email"
    const val IP_ADDRESS = "ipAddress"

    // 소프트 삭제 관련
    const val DELETED_EMAIL_PREFIX = "deleted:email:"
    const val DELETED_PHONE_PREFIX = "deleted:phone:"

    // Keycloak 관련 키
    const val KEYCLOAK_REFRESH_PREFIX = "keycloak:refresh:"
    const val KEYCLOAK_EMAIL_PREFIX = "keycloak:email:"
}