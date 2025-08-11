package kr.pincoin.api.app.oauth2.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 소셜 마이그레이션 응답
 */
data class SocialMigrationResponse(
    /**
     * 마이그레이션 상태
     * - NEW_USER: 신규 사용자 생성
     * - LINKED: 기존 소셜 전용 사용자와 Keycloak 연동
     * - ALREADY_LINKED: 이미 연동된 사용자
     * - MIGRATION_REQUIRED: 기존 패스워드 사용자, 수동 마이그레이션 필요
     */
    @field:JsonProperty("status")
    val status: MigrationStatus,

    /**
     * 사용자 ID
     */
    @field:JsonProperty("userId")
    val userId: Int,

    /**
     * 사용자 이메일
     */
    @field:JsonProperty("email")
    val email: String,

    /**
     * Keycloak 사용자 ID
     */
    @field:JsonProperty("keycloakId")
    val keycloakId: String,

    /**
     * 추가 메시지 (에러나 안내사항)
     */
    @field:JsonProperty("message")
    val message: String? = null
) {
    enum class MigrationStatus {
        NEW_USER,
        LINKED,
        ALREADY_LINKED,
        MIGRATION_REQUIRED
    }

    companion object {
        fun newUser(
            userId: Int,
            email: String,
            keycloakId: String
        ): SocialMigrationResponse = SocialMigrationResponse(
            status = MigrationStatus.NEW_USER,
            userId = userId,
            email = email,
            keycloakId = keycloakId,
            message = "새로운 소셜 사용자가 생성되었습니다."
        )

        fun linked(
            userId: Int,
            email: String,
            keycloakId: String
        ): SocialMigrationResponse = SocialMigrationResponse(
            status = MigrationStatus.LINKED,
            userId = userId,
            email = email,
            keycloakId = keycloakId,
            message = "기존 소셜 계정과 Keycloak이 연동되었습니다."
        )

        fun alreadyLinked(
            userId: Int,
            email: String,
            keycloakId: String
        ): SocialMigrationResponse = SocialMigrationResponse(
            status = MigrationStatus.ALREADY_LINKED,
            userId = userId,
            email = email,
            keycloakId = keycloakId,
            message = "이미 연동된 사용자입니다."
        )

        fun migrationRequired(
            userId: Int,
            email: String
        ): SocialMigrationResponse = SocialMigrationResponse(
            status = MigrationStatus.MIGRATION_REQUIRED,
            userId = userId,
            email = email,
            keycloakId = "",
            message = "기존 패스워드 계정이 존재합니다. 수동 마이그레이션이 필요합니다."
        )
    }
}