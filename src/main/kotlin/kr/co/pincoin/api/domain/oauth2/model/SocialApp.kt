package kr.co.pincoin.api.domain.oauth2.model

class SocialApp private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val provider: String,

    // 3. 도메인 로직 가변 필드
    name: String,
    clientId: String,
    secret: String,
    key: String,
) {
    var name: String = name
        private set

    var clientId: String = clientId
        private set

    var secret: String = secret
        private set

    var key: String = key
        private set

    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            name: String,
            clientId: String,
            secret: String,
            key: String,
        ): SocialApp =
            SocialApp(
                id = id,
                provider = provider,
                name = name,
                clientId = clientId,
                secret = secret,
                key = key,
            )
    }
}