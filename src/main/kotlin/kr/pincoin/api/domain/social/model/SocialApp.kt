package kr.pincoin.api.domain.social.model

class SocialApp private constructor(
    val id: Int? = null,
    val provider: String,
    val name: String,
    val clientId: String,
    val secret: String,
    val key: String
) {
    fun updateProvider(newProvider: String): SocialApp =
        copy(provider = newProvider)

    fun updateName(newName: String): SocialApp =
        copy(name = newName)

    fun updateClientId(newClientId: String): SocialApp =
        copy(clientId = newClientId)

    fun updateSecret(newSecret: String): SocialApp =
        copy(secret = newSecret)

    fun updateKey(newKey: String): SocialApp =
        copy(key = newKey)

    fun isValid(): Boolean =
        provider.isNotBlank() && name.isNotBlank() && clientId.isNotBlank()

    private fun copy(
        provider: String = this.provider,
        name: String = this.name,
        clientId: String = this.clientId,
        secret: String = this.secret,
        key: String = this.key
    ): SocialApp = SocialApp(
        id = this.id,
        provider = provider,
        name = name,
        clientId = clientId,
        secret = secret,
        key = key
    )

    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            name: String,
            clientId: String,
            secret: String,
            key: String
        ): SocialApp {
            require(provider.isNotBlank()) { "프로바이더는 필수 입력값입니다" }
            require(name.isNotBlank()) { "이름은 필수 입력값입니다" }
            require(clientId.isNotBlank()) { "클라이언트 ID는 필수 입력값입니다" }

            return SocialApp(
                id = id,
                provider = provider,
                name = name,
                clientId = clientId,
                secret = secret,
                key = key
            )
        }
    }
}