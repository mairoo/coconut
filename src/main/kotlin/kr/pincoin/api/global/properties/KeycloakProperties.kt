package kr.pincoin.api.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    val enabled: Boolean = false,

    val userMigration: UserMigration = UserMigration(),

    val realm: String = "pincoin",

    // 인증 전용 클라이언트
    val clientId: String = "pincoin-backend",

    val clientSecret: String = "",

    // 관리 전용 클라이언트
    val adminClientId: String = "realm-management",

    val adminClientSecret: String = "",

    val serverUrl: String = "http://keycloak:8080"
) {

    data class UserMigration(
        val strategy: String = "email-based",

        val autoCreate: Boolean = false
    )
}