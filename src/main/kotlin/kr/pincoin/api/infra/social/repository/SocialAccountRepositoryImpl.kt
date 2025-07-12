package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.domain.social.repository.SocialAccountRepository
import org.springframework.stereotype.Repository

@Repository
class SocialAccountRepositoryImpl(
    private val jpaRepository: SocialAccountJpaRepository,
    private val queryRepository: SocialAccountQueryRepository,
) : SocialAccountRepository {
}