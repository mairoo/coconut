package kr.pincoin.api.app.oauth2.enums

enum class SocialMigrationType {
    /**
     * 완전히 새로운 사용자가 생성됨
     */
    NEW_USER_CREATED,

    /**
     * 기존 레거시 사용자가 소셜 계정과 연동됨
     */
    EXISTING_USER_MIGRATED,

    /**
     * 이미 소셜 계정과 연동된 사용자의 정상 로그인
     */
    ALREADY_MIGRATED
}