package kr.co.pincoin.api.infra.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialAccount
import kr.co.pincoin.api.domain.oauth2.repository.SocialAccountRepository
import kr.co.pincoin.api.infra.oauth2.mapper.toEntity
import kr.co.pincoin.api.infra.oauth2.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAccountRepositoryImpl(
    private val jpaRepository: SocialAccountJpaRepository,
) : SocialAccountRepository {
    override fun save(
        socialAccount: SocialAccount,
    ): SocialAccount =
        socialAccount.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜 계정 저장 실패")
}