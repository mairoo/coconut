package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialToken
import kr.co.pincoin.api.domain.oauth2.repository.SocialTokenRepository
import org.springframework.stereotype.Repository

@Repository
class SocialTokenRepositoryImpl(
    private val jpaRepository: SocialTokenJpaRepository,
) : SocialTokenRepository {
    override fun save(socialToken: SocialToken): SocialToken {
        TODO("Not yet implemented")
    }
}