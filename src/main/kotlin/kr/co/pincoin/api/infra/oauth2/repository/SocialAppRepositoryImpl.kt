package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialApp
import kr.co.pincoin.api.domain.oauth2.repository.SocialAppRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAppRepositoryImpl(
    private val jpaRepository: SocialAppJpaRepository,
) : SocialAppRepository {
    override fun save(socialApp: SocialApp): SocialApp {
        TODO("Not yet implemented")
    }
}