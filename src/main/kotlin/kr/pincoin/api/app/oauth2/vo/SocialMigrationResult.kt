package kr.pincoin.api.app.oauth2.vo

import kr.pincoin.api.app.oauth2.enums.SocialMigrationType
import kr.pincoin.api.domain.user.model.User
import java.time.LocalDateTime

data class SocialMigrationResult(
    val type: SocialMigrationType,
    val user: User,
    val message: String?,
    val migratedAt: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun newUserCreated(user: User): SocialMigrationResult =
            SocialMigrationResult(
                type = SocialMigrationType.NEW_USER_CREATED,
                user = user,
                message = "환영합니다! 소셜 계정으로 새 계정이 생성되었습니다."
            )

        fun existingUserMigrated(user: User): SocialMigrationResult =
            SocialMigrationResult(
                type = SocialMigrationType.EXISTING_USER_MIGRATED,
                user = user,
                message = "기존 계정이 소셜 로그인과 안전하게 통합되었습니다."
            )

        fun alreadyMigrated(user: User): SocialMigrationResult =
            SocialMigrationResult(
                type = SocialMigrationType.ALREADY_MIGRATED,
                user = user,
                message = null
            )
    }
}