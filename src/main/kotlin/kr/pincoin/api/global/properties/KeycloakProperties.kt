package kr.pincoin.api.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    /**
     * 점진적 마이그레이션을 위한 활성화 플래그
     */
    val enabled: Boolean = false,

    /**
     * 사용자 마이그레이션 설정
     */
    val userMigration: UserMigration = UserMigration(),

    /**
     * Realm 이름
     */
    val realm: String = "pincoin",

    /**
     * 클라이언트 ID
     */
    val clientId: String = "pincoin-backend",

    /**
     * 클라이언트 Secret
     */
    val clientSecret: String = "",

    /**
     * Keycloak 서버 URL
     */
    val serverUrl: String = "http://keycloak:8080"
) {

    data class UserMigration(
        /**
         * 매핑 전략 (email-based, username-based 등)
         */
        val strategy: String = "email-based",

        /**
         * 자동 생성 여부
         */
        val autoCreate: Boolean = false
    )
}