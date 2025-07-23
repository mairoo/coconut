package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.SocialApp
import kr.pincoin.api.domain.social.repository.SocialAppRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAppRepositoryImpl(
    private val jpaRepository: SocialAppJpaRepository,
    private val queryRepository: SocialAppQueryRepository,
) : SocialAppRepository {
    override fun save(
        socialApp: SocialApp,
    ): SocialApp =
        socialApp.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜앱 저장 실패")

    override fun findById(
        id: Int,
    ): SocialApp? =
        queryRepository.findById(id)?.toModel()
}