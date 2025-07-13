package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.model.SocialAccount
import kr.pincoin.api.domain.social.repository.SocialAccountRepository
import kr.pincoin.api.infra.social.mapper.toEntity
import kr.pincoin.api.infra.social.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class SocialAccountRepositoryImpl(
    private val jpaRepository: SocialAccountJpaRepository,
    private val queryRepository: SocialAccountQueryRepository,
) : SocialAccountRepository {
    override fun save(
        socialAccount: SocialAccount,
    ): SocialAccount =
        socialAccount.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("소셜계정 저장 실패")

    override fun findById(
        id: Int,
    ): SocialAccount? {
        return queryRepository.findById(id)?.toModel()
    }
}