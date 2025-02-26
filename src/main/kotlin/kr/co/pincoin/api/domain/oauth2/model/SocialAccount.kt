package kr.co.pincoin.api.domain.oauth2.model

import java.time.ZonedDateTime

class SocialAccount private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val provider: String,
    val uid: String,
    val dateJoined: ZonedDateTime,
    val userId: Int,

    // 3. 도메인 로직 가변 필드
    lastLogin: ZonedDateTime,
    extraData: String,
) {
    var lastLogin: ZonedDateTime = lastLogin
        private set

    var extraData: String = extraData
        private set

    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            uid: String,
            lastLogin: ZonedDateTime = ZonedDateTime.now(),
            dateJoined: ZonedDateTime = ZonedDateTime.now(),
            extraData: String,
            userId: Int,
        ): SocialAccount =
            SocialAccount(
                id = id,
                provider = provider,
                uid = uid,
                lastLogin = lastLogin,
                dateJoined = dateJoined,
                extraData = extraData,
                userId = userId,
            )
    }
}