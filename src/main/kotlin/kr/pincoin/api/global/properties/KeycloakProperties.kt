package kr.pincoin.api.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    val enabled: Boolean = false,

    val userMigration: UserMigration = UserMigration(),

    val realm: String = "pincoin",

    val clientId: String = "pincoin-backend",

    val clientSecret: String = "",

    val serverUrl: String = "http://keycloak:8080"
) {

    data class UserMigration(
        val strategy: String = "email-based",

        val autoCreate: Boolean = false
    )
}