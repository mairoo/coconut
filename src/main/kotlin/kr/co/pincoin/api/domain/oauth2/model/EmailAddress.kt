package kr.co.pincoin.api.domain.oauth2.model

class EmailAddress private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val email: String,
    val userId: Int,

    // 3. 도메인 로직 가변 필드
    verified: Boolean,
    primary: Boolean,
) {
    var verified: Boolean = verified
        private set

    var primary: Boolean = primary
        private set

    companion object {
        fun of(
            id: Int? = null,
            email: String,
            verified: Boolean = false,
            primary: Boolean = false,
            userId: Int,
        ): EmailAddress =
            EmailAddress(
                id = id,
                email = email,
                verified = verified,
                primary = primary,
                userId = userId,
            )
    }
}