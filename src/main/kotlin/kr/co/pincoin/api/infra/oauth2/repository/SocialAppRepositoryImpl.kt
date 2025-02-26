package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialApp
import kr.co.pincoin.api.domain.oauth2.repository.SocialAppRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAppRepositoryImpl(
    private val jpaRepository: SocialAppJpaRepository,
) : SocialAppRepository {
    override fun save(socialApp: SocialApp): SocialApp =
        socialApp.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜 앱 저장 실패")
}