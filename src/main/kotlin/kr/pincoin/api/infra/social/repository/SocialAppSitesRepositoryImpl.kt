package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.SocialAppSites
import kr.pincoin.api.domain.social.repository.SocialAppSitesRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesRepositoryImpl(
    private val jpaRepository: SocialAppSitesJpaRepository,
    private val queryRepository: SocialAppSitesQueryRepository,
) : SocialAppSitesRepository {
    override fun save(
        socialAppSites: SocialAppSites,
    ): SocialAppSites =
        socialAppSites.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜앱사이트 저장 실패")

    override fun findById(
        id: Int,
    ): SocialAppSites? {
        return queryRepository.findById(id)?.toModel()
    }
}