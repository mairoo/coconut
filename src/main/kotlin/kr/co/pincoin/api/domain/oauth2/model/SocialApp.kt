package kr.co.pincoin.api.domain.oauth2.model

class SocialApp private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val provider: String,

    // 3. 도메인 로직 가변 필드
    val name: String,
    val clientId: String,
    val secret: String,
    val key: String,
) {
    fun update(
        newName: String? = null,
        newClientId: String? = null,
        newSecret: String? = null,
        newKey: String? = null
    ): SocialApp =
        copy(
            name = newName ?: name,
            clientId = newClientId ?: clientId,
            secret = newSecret ?: secret,
            key = newKey ?: key
        )

    private fun copy(
        name: String? = null,
        clientId: String? = null,
        secret: String? = null,
        key: String? = null
    ): SocialApp = SocialApp(
        id = this.id,
        provider = this.provider,
        name = name ?: this.name,
        clientId = clientId ?: this.clientId,
        secret = secret ?: this.secret,
        key = key ?: this.key
    )

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