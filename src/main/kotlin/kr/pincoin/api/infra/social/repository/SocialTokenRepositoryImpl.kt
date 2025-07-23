package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.SocialToken
import kr.pincoin.api.domain.social.repository.SocialTokenRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialTokenRepositoryImpl(
    private val jpaRepository: SocialTokenJpaRepository,
    private val queryRepository: SocialTokenQueryRepository,
) : SocialTokenRepository {
    override fun save(
        socialToken: SocialToken,
    ): SocialToken =
        socialToken.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜토큰 저장 실패")

    override fun findById(
        id: Int,
    ): SocialToken? =
        queryRepository.findById(id)?.toModel()
}