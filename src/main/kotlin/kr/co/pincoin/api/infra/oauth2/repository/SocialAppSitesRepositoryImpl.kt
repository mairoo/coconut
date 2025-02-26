package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialAppSites
import kr.co.pincoin.api.domain.oauth2.repository.SocialAppSitesRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesRepositoryImpl(
    private val jpaRepository: SocialAppSitesJpaRepository
) : SocialAppSitesRepository {
    override fun save(
        socialAppSites: SocialAppSites,
    ): SocialAppSites =
        socialAppSites.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜 앱 사이트 저장 실패")
}