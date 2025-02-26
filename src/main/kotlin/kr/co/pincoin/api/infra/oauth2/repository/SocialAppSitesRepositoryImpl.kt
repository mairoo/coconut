package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialAppSites
import kr.co.pincoin.api.domain.oauth2.repository.SocialAppSitesRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesRepositoryImpl(
    private val jpaRepository: SocialAppSitesJpaRepository
) : SocialAppSitesRepository {
    override fun save(socialAppSites: SocialAppSites): SocialAppSites {
        TODO("Not yet implemented")
    }
}