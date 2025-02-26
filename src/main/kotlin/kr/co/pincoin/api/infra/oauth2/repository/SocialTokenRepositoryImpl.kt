package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialToken
import kr.co.pincoin.api.domain.oauth2.repository.SocialTokenRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialTokenRepositoryImpl(
    private val jpaRepository: SocialTokenJpaRepository,
) : SocialTokenRepository {
    override fun save(
        socialToken: SocialToken,
    ): SocialToken =
        socialToken.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜 토큰 저장 실패")
}